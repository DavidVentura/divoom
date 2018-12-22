#!/usr/bin/env python3
import struct
from enum import Enum

class _PROTO:
    START_BYTE = [0x01]
    END_BYTE = [0x02]

class Commands(Enum):
    STATIC_IMG = [0x44, 0x00, 0x0a, 0x0a, 0x04]
    SWITCH_VIEW = [0x45]
    RADIO_ON = [0x05, 0x01]
    RADIO_OFF = [0x05, 0x00]
    MUTE = [0x0a, 0x00]
    UNMUTE = [0x0a, 0x01]
    BRIGHTNESS = [0x32]
    SET_VOL = [0x08]
    GET_VOL = [0x09]
    GET_MUTE = [0x0b]
    SET_DATE = [0x18]

class Views(Enum):
    TEMP_C = [0x01, 0x00]
    TEMP_F = [0x01, 0x01]
    CLOCK_12 = [0x00, 0x00]
    CLOCK_24 = [0x00, 0x01]
    STOPWATCH = [0x06, 0x00]
    SCORE = [0x07, 0x00]

class Arguments(Enum):
    NONE = []
    BR_HIGH = [0xd2]
    BR_LOW = [0x3f]
    BR_OFF = [0x00]

class Replies(Enum):
    VOL = [0x06, 0x00, 0x04, 0x09, 0x55]
    MUTE = [0x0b]
    TEMP = [0x59]
    RADIO_FREQ = [0x60]

class Command:

    def __init__(self, action, args, extra_bytes=[]):
        self._action = action
        self._args = args

        message = [*(action.value)]
        if args:
            message.extend(args.value)
        if extra_bytes:
            message.extend(extra_bytes)
        #print('message', nice(message))

        _masked = mask(message)
        _length = message_length_b(message)
        _ck = mask(checksum(_length + message))
        self._command = _PROTO.START_BYTE + _length + _masked + _ck + _PROTO.END_BYTE

    @property
    def args(self):
        return self._args.name

    @property
    def action(self):
        return self._action.name

    @property
    def command(self):
        return self._command

    def __repr__(self):
        return "%s %s" % (self.action, self.args)


def nice(message):
    return "[%s]" % ', '.join(hex(b) for b in message)

def message_length_b(message):
    # 2 extra bytes to store the length
    _length = len(message) + 2
    return list(struct.pack('<h', _length))

def checksum(message):
    _bytes = sum(message).to_bytes(2, byteorder='little')
    #print('checksumming', nice(message), 'to', nice(_bytes))
    return list(_bytes)

def mask(_bytes):
    ret = []
    for b in _bytes:
        if b in (1, 2):
            ret.append(0x03)
            ret.append(b + 3)
        else:
            ret.append(b)
    #print('MASK: orig:', nice(_bytes), 'ret', nice(ret))
    return ret

def unmask(_bytes):
    ret = []
    i = 0
    while i < len(_bytes):
        b = _bytes[i]
        if b == 0x3:
            b = _bytes[i+1] - 0x3
            i += 1
        ret.append(b)
        i += 1
    return ret

def test_mask():
    assert mask([0x04, 0x05]) == [0x04, 0x05]
    assert mask([0x01, 0x05]) == [0x03, 0x04, 0x05]
    assert mask([0x01, 0x02]) == [0x03, 0x04, 0x03, 0x05]

def test_unmask():
    assert unmask([0x04, 0x05]) == [0x04, 0x05]
    assert unmask([0x03, 0x04, 0x05]) == [0x01, 0x05]
    assert unmask([0x03, 0x04, 0x03, 0x05]) == [0x01, 0x02]

def test_mask_undoes_unmask():
    assert unmask(mask([0x04, 0x05])) == [0x04, 0x05]
    assert mask(unmask([0x03, 0x04, 0x05])) == [0x03, 0x04, 0x05]
    assert mask(unmask([0x03, 0x04, 0x03, 0x05])) == [0x03, 0x04, 0x03, 0x05]

def test_checksum():
    assert checksum([0x05, 0x00, 0x45, 0x00, 0x01]) == [0x4b, 0x00]

def test_command():
    c = Command(Commands.SWITCH_VIEW, Views.CLOCK_24)
    assert c.command == [0x01, 0x05, 0x00, 0x45, 0x00, 0x03, 0x04, 0x4B, 0x00, 0x02]
    c = Command(Commands.SWITCH_VIEW, Views.CLOCK_24, [0xff, 0x00, 0x00])
    assert c.command == [0x01, 0x08, 0x00, 0x45, 0x00, 0x03, 0x04, 0xff, 0x00, 0x00, 0x4D, 0x03, 0x04, 0x02]

if __name__ == '__main__':
    test_mask()
    test_unmask()
    test_mask_undoes_unmask()
    test_checksum()
    test_command()
