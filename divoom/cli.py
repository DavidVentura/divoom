#!/usr/bin/env python3
import argparse
import time

from divoom.protocol import Brightness, SwitchView, Views, BrightnessValues, AudioMode, SetMode, SetRadio
from divoom.device import Timebox, DitooPro

#commands = [Command(Commands.BRIGHTNESS, Brightness.HIGH),
#            Command(Commands.BRIGHTNESS, Brightness.LOW),
#            Command(Commands.BRIGHTNESS, Brightness.OFF),
#            Command(Commands.BRIGHTNESS, Brightness.HIGH),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_C, extra_bytes=[0x00, 0xff, 0x00]),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_C, extra_bytes=[0x00, 0xff, 0xff]),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_C, extra_bytes=[0xff, 0xff, 0x00]),
#            Command(Commands.SWITCH_VIEW, Views.TEMP_F),
#            Command(Commands.SWITCH_VIEW, Views.CLOCK_12),
#            Command(Commands.SWITCH_VIEW, Views.CLOCK_24),
#           ]

def valid_fm_freq(val):
    try:
        val = float(val)
    except Exception:
        raise argparse.ArgumentTypeError(f"FM frequencies must be numbers (ie: 107.5)")

    if val < 87.5 or val > 108.0:
        raise argparse.ArgumentTypeError(f"FM frequencies must be between 87.5 and 108.0")
    return val

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--address', required=True)
    parser.add_argument('--device', required=True, choices=['TimeBox', 'DitooPro'])

    subparsers = parser.add_subparsers(dest='command')

    br_sub = subparsers.add_parser('brightness')
    br_sub.add_argument('value', choices=[e.name.lower() for e in BrightnessValues])

    view_sub = subparsers.add_parser('view')
    view_sub.add_argument('value', choices=[e.name.lower() for e in Views])

    set_audio_mode = subparsers.add_parser('set_audio_mode')
    set_audio_mode.add_argument('value', choices=[e.name.lower() for e in AudioMode])

    set_radio_sub = subparsers.add_parser('set_radio_freq')
    set_radio_sub.add_argument('value', type=valid_fm_freq)

    return parser.parse_args()

def main():
    args = parse_args()
    commands = []

    if args.command == 'brightness':
        # Brightness(Brightness.OFF)
        commands = [Brightness(BrightnessValues[args.value.upper()])]
    if args.command == 'view':
        # View
        commands = [SwitchView(Views[args.value.upper()])]
    if args.command == 'set_audio_mode':
        commands = [SetMode(AudioMode[args.value.upper()])]
    if args.command == 'set_radio_freq':
        commands = [SetRadio(args.value)]
    cls = None
    if args.device == 'TimeBox':
        cls = Timebox
    if args.device == 'DitooPro':
        cls = DitooPro

    assert cls

    if not commands:
        print("Nothing to do")
        return

    with cls(args.address) as d:
        for command in commands:
            d.send(command)
            time.sleep(0.1)
