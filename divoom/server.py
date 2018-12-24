#!/usr/bin/env python3
import redis
import time
import threading
import json

from divoom.device import Device
from divoom.protocol import valid_command, Replies

def handle_command(device):
    def _handle_command(message):
        data = message['data']
        mlst = list(data)
        if not valid_command(mlst):
            print(message)
            print(data, 'is not a valid command')
            return
        print(data)
        device.send(data, raw=True)
    return _handle_command

def handle_replies(redis, device):
    while True:
        replies = device.get_message()
        for (_type, value) in replies:
            data = {'VALUE': value}
            if type(_type) is Replies:
                data['TYPE'] = _type.name
            else:
                data['TYPE'] = _type
            print(data)
            redis.publish('replies', json.dumps(data))

def main():
    r = redis.Redis(host='localhost', port=6379, db=0)
    p = r.pubsub()
    d = Device('11:75:58:78:DB:05', timeout=None)
    d._connect()
    print('BT Connected!')
    # Should replies be on a queue handled by the device?
    t = threading.Thread(target=handle_replies, args=(r, d))
    t.daemon = True
    t.start()
    p.subscribe(commands=handle_command(d))

    try:
        while True:
            p.get_message()
            time.sleep(0.1)
    except KeyboardInterrupt:
        pass
    finally:
        print('Dc')
        d._disconnect()
        print('join')
        t.join()
        print('done')

if __name__ == '__main__':
    main()
