#!/usr/bin/env python3
import abc
import struct
from enum import Enum
from .image import DitooProImage


class _PROTO:
    START_BYTE = bytes([0x01])
    END_BYTE = bytes([0x02])


class BrightnessValues(Enum):
    NO_CHANGE = []
    HIGH = [0xD2]
    LOW = [0x3F]
    OFF = [0x00]

class Command(abc.ABC):
    @abc.abstractmethod
    def encode(self) -> bytes:
        ...

class Brightness(Command):
    def __init__(self, value: BrightnessValues):
        self.value = value

    def encode(self) -> bytes:
        return bytes(Commands.BRIGHTNESS.value + self.value.value)

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

class SwitchView(Command):
    def __init__(self, view: Views):
        self.view = view
    def encode(self) -> bytes:
        return bytes(Commands.SWITCH_VIEW.value + self.view.value)

class AudioMode(Enum):
    # "Radio ON"
    RADIO = [0x01]
    BLUETOOTH = [0x00]

class SetMode(Command):
    def __init__(self, mode: AudioMode):
        self.mode = mode
    def encode(self):
        return bytes(Commands.AUDIO_MODE.value + self.mode.value)

class SetRadio(Command):
    def __init__(self, freq: float):
        self.freq = freq
    def encode(self):
        return bytes(Commands.SET_RADIO.value + freq_to_bytes(self.freq))

class Errors(Enum):
    RADIO_NOT_ON = [0xB]

class Commands(Enum):
    STATIC_IMG = [0x44, 0x00, 0x0A, 0x0A, 0x04]
    SWITCH_VIEW = [0x45]
    AUDIO_MODE = [0x05]
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
    return None, None


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
    failed_command, error = error_reply(_bytes)
    if error:
        if error == Errors.RADIO_NOT_ON.value:
            raise ValueError(f"Radio is not ON")
        raise ValueError(f"Got error {error}")

    _, *payload, _ = _bytes
    payload = unmask(payload)
    # 0x61 0xaa 0xb 0x3 0x4
    payload = payload[:-2]  # discard checksum
    # 0x61 0xaa 0xb
    print(payload)
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
    return struct.pack("<h", _length)


def checksum(message):
    _bytes = sum(message).to_bytes(2, byteorder="little")
    return _bytes


def freq_to_bytes(freq):
    freq = freq * 10  # 100.3 => 1003
    # 88.1 => 881
    if freq > 1000:
        ret = [int(freq - 1000), int(freq / 100)]
    else:
        ret = [int(freq % 100), int(freq / 100)]
    print(ret)
    return ret


def bytes_to_freq(_bytes):
    assert len(_bytes) == 2
    first = _bytes[1] * 10
    second = _bytes[0] / 10
    return first + second
