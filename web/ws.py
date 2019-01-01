#!/usr/bin/env python3
import json
import time
import redis

from divoom.image import solid_color, _pack_tuples_to_image
from divoom.protocol import Commands, Command, freq_to_bytes
from queue import Queue
from threading import Thread
from websocket_server import WebsocketServer

state = {}

def new_client(client, server):
    server.send_message_to_all(json.dumps(state))

def str_to_command(command):
    for c in Commands:
        if c.name == command:
            return c

def transform_value(command, value):
    if command == Commands.STATIC_IMG:
        return _pack_tuples_to_image(value)
    elif command == Commands.SET_RADIO:
        return freq_to_bytes(value)
    else:
        return value
def msg_received_ws(q):
    def wrap(client, server, msg):
        try:
            msg = json.loads(msg)
        except:
            print("Got invalid json from client (%s)" % msg)
            return

        if 'command' not in msg or 'value' not in msg:
            print("Got invalid json from client (%s) (no 'command' or 'value')" % msg)
            return
        command = str_to_command(msg['command'])
        if command is None:
            print("Invalid command: %s" % msg)
            return

        value = transform_value(command, msg['value'])
        print("Got %s from a ws client; putting to queue" % msg)
        print(value)
        q.put((command, value))
    return wrap

def handle_replies_redis(r_q):
    def _handle_replies_redis(message):
        m = message['data'].decode('ascii')
        data = json.loads(m)
        state.update(data)
        r_q.put(m)
    return _handle_replies_redis

def main():
    r_q = Queue()
    ws_q = Queue()
    server = WebsocketServer(8998, host='0.0.0.0') #, loglevel=logging.INFO)
    server.set_fn_new_client(new_client)
    server.set_fn_message_received(msg_received_ws(ws_q))

    t = Thread(target=server.run_forever)
    t.daemon = True
    t.start()

    r = redis.Redis(host='octopi.labs', port=6379, db=0)
    p = r.pubsub()
    p.subscribe(replies=handle_replies_redis(r_q))

    while True:
        if not ws_q.empty():
            comm, data = ws_q.get()
            print('from ws', comm, data)
            if type(data) is list:
                c = Command(comm, None, data)
            else:
                c = Command(comm, None, [data])
            print(c, c.command)
            r.publish('commands', bytes(c.command))
        if not r_q.empty():
            data = r_q.get()
            print('from redis', data)
            server.send_message_to_all(data)
        p.get_message()
        time.sleep(0.1)
