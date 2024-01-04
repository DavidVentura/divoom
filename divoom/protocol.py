#!/usr/bin/env python3
import struct
from enum import Enum
from .image import DitooProImage


class _PROTO:
    START_BYTE = [0x01]
    END_BYTE = [0x02]


class Commands(Enum):
    STATIC_IMG = [0x44, 0x00, 0x0A, 0x0A, 0x04]
    SWITCH_VIEW = [0x45]
    RADIO_ON = [0x05, 0x01]
    RADIO_OFF = [0x05, 0x00]
    MUTE = [0x0A, 0x00]
    UNMUTE = [0x0A, 0x01]
    # BRIGHTNESS = [0x32]
    BRIGHTNESS = [0x74]
    SET_VOL = [0x08]
    GET_VOL = [0x09]
    GET_TEMP = [0x59]
    GET_MUTE = [0x0B]
    SET_DATE = [0x18]
    GET_RADIO = [0x60]
    SET_RADIO = [0x61]
    SET_NOTIF = [0x50]
    SET_WEATHER = [0x5F]

    DITOOPRO_SHOW_ANIM = [0x8B]


class Views(Enum):
    CLOCK_12 = [0x00, 0x00]
    CLOCK_24 = [0x00, 0x01]
    TEMP_C = [0x01, 0x00]
    TEMP_F = [0x01, 0x01]
    ANIM_HC = [0x02]
    OFF = [0x03]
    EQ = [0x04]
    ANIM_DYN = [0x05]
    STOPWATCH = [0x06, 0x00]
    SCORE = [0x07, 0x00]


class Arguments(Enum):
    NONE = []
    BR_HIGH = [0xD2]
    BR_LOW = [0x3F]
    BR_OFF = [0x00]


class Replies(Enum):
    GET_VOL = 0x08
    SET_VOL = 0x09
    MUTE = 0x0B
    TEMP = 0x59
    RADIO_FREQ = 0x60
    SET_RADIO_FREQ = 0x61
    _SWITCH_VIEW = 0x45
    SWITCH_VIEW = 0x46
    UNKNOWN = 0x0


class Command:
    def __init__(self, action, args, extra_bytes=[], escaped: bool = True):
        self._action = action
        self._args = args

        message = [*(action.value)]
        if args:
            message.extend(args.value)
        if extra_bytes:
            message.extend(extra_bytes)

        _message_m = mask(message) if escaped else message
        _length = message_length_b(message)
        _length_m = mask(_length) if escaped else _length

        _ck = checksum(_length + message)
        if escaped:
            _ck = mask(_ck)
        self._command = (
            _PROTO.START_BYTE + _length_m + _message_m + _ck + _PROTO.END_BYTE
        )

    @property
    def args(self):
        if self._args:
            return self._args.name
        else:
            return "<>"

    @property
    def action(self):
        return self._action.name

    @property
    def command(self):
        return self._command

    def __repr__(self):
        return "%s %s" % (self.action, self.args)

    @classmethod
    def from_bytes(cls, _bytes):
        _bytes = _bytes[3:-3]  # head, length, length, *, cksum, cksum, tail
        command = None
        for c in Commands:
            if c.value[0] == _bytes[0]:
                command = c
        if command is None:
            raise ValueError("%s is an invalid command" % _bytes[0])

        extra_bytes = []
        if len(_bytes) > 1:
            extra_bytes = _bytes[1:]
        return cls(command, None, extra_bytes)


def valid_command(_bytes):
    return (
        len(_bytes) > 2
        and _bytes[0] == _PROTO.START_BYTE[0]
        and _bytes[-1] == _PROTO.END_BYTE[0]
    )
    # TODO validate checksum


def valid_reply(_bytes):
    return (
        len(_bytes) >= 6
        and valid_command(_bytes)
        and (_bytes[2] == 0xAA or (_bytes[3] == 0x4 and _bytes[5] == 0x55))
    )


def error_reply(_bytes):
    if _bytes[2] == 0xAA:
        return _bytes[1], _bytes[3:-3]
    # 3 => -3 to convert
    # [head, command, 0xaa, _, cksum1, cksum2, tail] => [_]


def split_reply(_bytes):
    ret = []
    start = 0
    for i in range(0, len(_bytes)):
        c = _bytes[i]
        if c == _PROTO.END_BYTE[0]:
            new_reply = _bytes[start : i + 1]
            if valid_reply(new_reply):
                ret.append(new_reply)
            start = i + 1
    return ret


def parse_reply_data(_type, _bytes):
    data = _bytes
    if _type in (Replies.SET_VOL, Replies.GET_VOL):
        data = _bytes[0]
        _type = Replies.GET_VOL
    elif _type in (Replies.RADIO_FREQ,):  # Replies.SET_RADIO_FREQ,
        print(_type, _bytes)
        data = bytes_to_freq(_bytes[:2])
        _type = Replies.RADIO_FREQ
    elif _type == Replies.TEMP:
        data = _bytes[1]
    elif _type == Replies.MUTE:
        data = bool(_bytes[0])
    elif _type == Replies.SWITCH_VIEW:
        first_part = _bytes[1:10]
        first_part[3] = -1  # Anim 1
        first_part[7] = -1  # Brightness
        first_part[8] = -1  # Anim2

        data = {
            "CLOCK_COLOR": _bytes[10:13],
            "TEMP_COLOR": _bytes[13:16],
            "BRIGHT": _bytes[8],
            "VIEW_ID": _bytes[0],
            "ANIM1": _bytes[3],
            "ANIM2": _bytes[9],
            "FIRST_PART": first_part,
            "LAST_PART": _bytes[16:],
        }

    return _type, data


def parse_reply(_bytes):
    assert valid_command(_bytes)
    head, *payload, tail = _bytes
    payload = unmask(payload)
    payload = payload[:-2]  # discard checksum
    length = int.from_bytes(payload[0:1], "little")
    command = payload[3]  # payload[2] == 0x4, payload[4] == 0x55
    data = payload[5:]
    print("command", hex(command), "data", data)

    for r in Replies:
        if command == r.value:
            return parse_reply_data(r, data)
    return command, data


def message_length_b(message):
    # 2 extra bytes to store the length
    _length = len(message) + 2
    return list(struct.pack("<h", _length))


def checksum(message):
    _bytes = sum(message).to_bytes(2, byteorder="little")
    return list(_bytes)


def mask(_bytes):
    ret = []
    for b in _bytes:
        if b in (0x1, 0x2, 0x3):
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
            b = _bytes[i + 1] - 0x3
            i += 1
        ret.append(b)
        i += 1
    return ret


def freq_to_bytes(freq):
    freq = freq * 10  # 100.3 => 1003
    # 88.1 => 881
    if freq > 1000:
        ret = [int(freq - 1000), int(freq / 100)]
    else:
        ret = [int(freq % 100), int(freq / 100)]
    return ret


def bytes_to_freq(_bytes):
    assert len(_bytes) == 2
    first = _bytes[1] * 10
    second = _bytes[0] / 10
    return first + second


class DitooProShowImageCommand(Command):
    def __init__(self, image: DitooProImage):
        self.image = image

        super().__init__(Commands.DITOOPRO_SHOW_ANIM, None, image.serialize(), False)
