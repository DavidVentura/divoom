#!/usr/bin/env python3
import json
import time
import redis

from divoom.image import solid_color
from divoom.protocol import Commands
from queue import Queue
from threading import Thread
from websocket_server import WebsocketServer

state = {}

def new_client(client, server):
    server.send_message_to_all(json.dumps(state))

def msg_received_ws(q):
    def wrap(client, server, msg):
        try:
            msg = json.loads(msg)
        except:
            print("Got invalid json from client (%s)" % msg)
            return
        print("Got %s from a ws client; putting to queue" % msg)
        q.put(msg)
    return wrap

def handle_replies_redis(r_q):
    def _handle_replies_redis(message):
        m = message['data'].decode('ascii')
        data = json.loads(m)
        print('from redis', data)
        state.update(data)
        r_q.put(m)

def main():
    r_q = Queue()
    ws_q = Queue()
    server = WebsocketServer(8998, host='0.0.0.0') #, loglevel=logging.INFO)
    server.set_fn_new_client(new_client)
    server.set_fn_message_received(msg_received_ws(ws_q))

    t = Thread(target=server.run_forever)
    t.daemon = True
    t.start()

    r = redis.Redis(host='localhost', port=6379, db=0)
    p = r.pubsub()
    p.subscribe(replies=handle_replies_redis(r_q))

    while True:
        if not ws_q.empty():
            data = ws_q.get()
            print('from ws', data)
            r.publish('commands', bytes(data))
        if not r_q.empty():
            data = r_q.get()
            print('from redis', data)
            server.send_message_to_all(daa)
        p.get_message()
        time.sleep(1)
