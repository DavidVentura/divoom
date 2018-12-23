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
    GET_TEMP = [0x59]
    GET_MUTE = [0x0b]
    SET_DATE = [0x18]
    GET_RADIO = [0x60]
    SET_RADIO = [0x61]

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
    VOL = [0x09]
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

        _masked = mask(message)
        _length = message_length_b(message)
        _ck = mask(checksum(_length + message))
        self._command = _PROTO.START_BYTE + _length + _masked + _ck + _PROTO.END_BYTE

    @property
    def args(self):
        if self._args:
            return self._args.name
        else:
            return '<>'

    @property
    def action(self):
        return self._action.name

    @property
    def command(self):
        return self._command

    def __repr__(self):
        return "%s %s" % (self.action, self.args)


def valid_reply(_bytes):
    return len(_bytes) > 6 and\
            _bytes[0] == _PROTO.START_BYTE[0] and\
            _bytes[-1] == _PROTO.END_BYTE[0] and\
            _bytes[3] == 0x4 and\
            _bytes[5] == 0x55

def split_reply(_bytes):
    ret = []
    start = 0
    for i in range(0, len(_bytes)):
        c = _bytes[i]
        if c == _PROTO.END_BYTE[0]:
            new_reply = _bytes[start:i+1]
            if valid_reply(new_reply):
                ret.append(new_reply)
            start = i + 1
    return ret

def parse_reply(_bytes):
    assert _bytes[0] == _PROTO.START_BYTE[0]
    assert _bytes[-1] == _PROTO.END_BYTE[0]

    head, *payload, tail = _bytes
    payload = unmask(payload)
    payload = payload[:-2] # discard checksum
    length = int.from_bytes(payload[0:1], 'little')
    command = payload[3] # payload[2] == 0x4, payload[4] == 0x55
    data = payload[5:]
    print('length', length, 'command', command, 'data', data)

    for r in Commands:
        if command == r.value[0]:
            return r, data

def nice(message):
    return "[%s]" % ', '.join(hex(b) for b in message)

def message_length_b(message):
    # 2 extra bytes to store the length
    _length = len(message) + 2
    return list(struct.pack('<h', _length))

def checksum(message):
    _bytes = sum(message).to_bytes(2, byteorder='little')
    return list(_bytes)

def mask(_bytes):
    ret = []
    for b in _bytes:
        if b in (0x1, 0x2):
            ret.append(0x03)
            ret.append(b + 3)
        else:
            ret.append(b)
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

def freq_to_bytes(freq):
    freq = freq * 10 # 100.3 => 1003
                     # 88.1 => 881
    if freq > 1000:
        ret = [freq - 1000, int(freq / 100)]
    else:
        ret = [freq % 100, int(freq / 100)]
    return ret

def bytes_to_freq(_bytes):
    assert len(_bytes) == 2
    first = _bytes[1] * 10
    second = _bytes[0] / 10
    return first + second

def test_bytes_to_freq():
    assert bytes_to_freq([0x03, 0x0A]) == 100.3
    assert bytes_to_freq([0x4f, 0xa]) == 107.9 # 4f == 79
    assert bytes_to_freq([0x51, 0x8]) == 88.1 # 0x51 == 81

def test_freq_to_bytes():
    assert freq_to_bytes(100.3) == [0x03, 0x0A]
    assert freq_to_bytes(100.9) == [0x09, 0x0A]
    assert freq_to_bytes(107.3) == [73, 0x0A]
    assert freq_to_bytes(90.3) == [0x03, 0x09]
    assert freq_to_bytes(90.9) == [0x09, 0x09]
    assert freq_to_bytes(97.3) == [73, 0x09]

def test_freq_to_bytes_to_freq():
    assert bytes_to_freq(freq_to_bytes(100.3)) == 100.3
    assert bytes_to_freq(freq_to_bytes(90.3)) == 90.3
    assert bytes_to_freq(freq_to_bytes(88.3)) == 88.3

def test_bytes_to_freq_to_bytes():
    assert freq_to_bytes(bytes_to_freq([73, 0x09])) == [73, 0x09]

def test_valid_reply():
    assert valid_reply([0x1, None, None, 0x4, None, 0x55, None, None, None, 0x2])
    assert valid_reply([0x1, 0x6, 0x0, 0x4, 0x8, 0x55, 0x9, 0x70, 0x0, 0x2])
    assert valid_reply([None, 0x6, 0x0, 0x4, 0x8, 0x55, 0x9, 0x70, 0x0, 0x2]) ==  False
    assert valid_reply([0x1, 0x6, 0x0, 0x4, 0x8, 0x55, 0x9, 0x70, 0x0, None]) ==  False
    assert valid_reply([0x1, 0x6, 0x0, None, 0x8, 0x55, 0x9, 0x70, 0x0, 0x2]) ==  False
    assert valid_reply([0x1, 0x6, 0x0, 0x4, 0x8, None, 0x9, 0x70, 0x0, 0x2]) ==  False

def test_split_reply():
    replies = split_reply([0x1, 0x8, 0x0, 0x4, 0x59, 0x55, 0x3, 0x4, 0x4b, 0x0, 0x6, 0x3, 0x4, 0x2,
                           0x1, 0x6, 0x0, 0x4, 0x32, 0x55, 0xd2, 0x63, 0x3, 0x4, 0x2,
                           0x1, 0x8, 0x0, 0x4, 0x59, 0x55, 0x3, 0x4, 0x49, 0x0, 0x4, 0x3, 0x4, 0x2])

    assert len(replies) == 3
    for reply in replies:
        assert len(reply) >= 6
        assert reply[0] == _PROTO.START_BYTE[0]
        assert reply[-1] == _PROTO.END_BYTE[0]


    replies = split_reply([0x1, 0x6, 0x0, 0x4, 0x8, 0x55, 0x9, 0x70, 0x0, 0x2])
    assert len(replies) == 1

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
    test_valid_reply()
    test_split_reply()
    test_freq_to_bytes()
    test_bytes_to_freq()
    test_freq_to_bytes_to_freq()
    test_bytes_to_freq_to_bytes()
