#!/usr/bin/env python3
from device import Device
from protocol import Command, Commands, Views, Arguments
from image import solid_color
import time

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
with Device('11:75:58:78:DB:05') as d:
    for c in commands:
        print(c, flush=True)
        d.send(c.command)
        time.sleep(3)
    input('ready to exit..')
