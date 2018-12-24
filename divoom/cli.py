#!/usr/bin/env python3
from divoom.protocol import Command, Commands, Views, Arguments, parse_reply, split_reply, freq_to_bytes
from divoom.image import solid_color
import json
import time
import threading
import redis
import queue
import random

#commands = [Command(Commands.BRIGHTNESS, Arguments.BR_HIGH),
#            Command(Commands.BRIGHTNESS, Arguments.BR_LOW),
#            Command(Commands.BRIGHTNESS, Arguments.BR_OFF),
#            Command(Commands.BRIGHTNESS, Arguments.BR_HIGH),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_C, extra_bytes=[0x00, 0xff, 0x00]),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_C, extra_bytes=[0x00, 0xff, 0xff]),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_C, extra_bytes=[0xff, 0xff, 0x00]),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_F),
#            Command(Commands.SWITCH_VIEW, Views.CLOCK_12),
#            Command(Commands.SWITCH_VIEW, Views.CLOCK_24),
#           ]

commands = [
            Command(Commands.BRIGHTNESS, Arguments.BR_HIGH),
            Command(Commands.STATIC_IMG, Arguments.NONE, extra_bytes=solid_color(0xf,0,0)),
            Command(Commands.BRIGHTNESS, Arguments.BR_OFF),
            Command(Commands.BRIGHTNESS, Arguments.BR_LOW),
            Command(Commands.BRIGHTNESS, Arguments.BR_HIGH),
           ]

commands = [
            Command(Commands.GET_VOL, None),
            Command(Commands.GET_VOL, None),
            Command(Commands.GET_VOL, None),
            Command(Commands.GET_RADIO, None),
            Command(Commands.GET_RADIO, None),
            Command(Commands.GET_RADIO, None),
            Command(Commands.SET_RADIO, None, freq_to_bytes(100.3)),
            Command(Commands.SET_RADIO, None, freq_to_bytes(100.5)),
            Command(Commands.SET_RADIO, None, freq_to_bytes(100.7)),
            Command(Commands.GET_RADIO, None),
            Command(Commands.GET_RADIO, None),
            Command(Commands.GET_RADIO, None),
           ]

def handle_replies(message):
    data = json.loads(message['data'].decode('ascii'))
    print(data)

def main():
    r = redis.Redis(host='localhost', port=6379, db=0)
    p = r.pubsub()
    p.subscribe(replies=handle_replies)

    while True:
        b = Command(Commands.SET_RADIO, None, freq_to_bytes(random.randrange(890, 1079) / 10)).command
        r.publish('commands', bytes(b))
        p.get_message()
        time.sleep(1)
