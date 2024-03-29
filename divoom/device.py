import struct
import dataclasses
import logging
import socket
from typing import Iterable
from enum import Enum
from divoom.protocol import parse_reply, split_reply, DitooProImage, Command, Commands

logger = logging.getLogger(__name__)


class Type(Enum):
    DEFAULT = 0
    DITOO_PRO = 1


@dataclasses.dataclass
class Config:
    port: int
    hello_bytes: list[int] | None
    size: int


CONFIG_MAP = {
    # port, hello bytes, size
    Type.DEFAULT: Config(4, [0x0, 0x5, 0x48, 0x45, 0x4C, 0x4C, 0x4F, 0x0], 11),
    Type.DITOO_PRO: Config(2, None, 16),
}


class Device:
    def __init__(self, addr: str, type_: Type = Type.DEFAULT, timeout=1):
        self.sock = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM, socket.BTPROTO_RFCOMM)
        self.addr = addr
        self.timeout = timeout
        self.type_ = type_

    def _connect(self):
        cfg = CONFIG_MAP[self.type_]
        self.sock.connect((self.addr, cfg.port))

        if cfg.hello_bytes is not None:
            resp = list(self.sock.recv(256))
            assert resp == cfg.hello_bytes

    def _disconnect(self):
        self.sock.close()

    def send(self, package, raw=False):
        self.sock.send(bytes(package))
        logger.debug("> %s", map(hex, package))

        if not raw:
            return self.get_message()

    def get_message(self):
        recv = self.sock.recv(256)  # Might receive many replies
        logger.debug("< %s", map(hex, recv))
        return self.__parse_reply(recv)

    def __enter__(self):
        self._connect()
        return self

    def __exit__(self, *args):
        print("Disconnecting")
        self._disconnect()

    def __parse_reply(self, _bytes):
        ret = []
        replies = split_reply(list(_bytes))
        for reply in replies:
            r = parse_reply(reply)
            ret.append(r)
        return ret

    def set_brightness(self, brightness) -> None:
        pass


class Timebox(Device):
    def __init__(self, addr: str, timeout: int = 1) -> None:
        super().__init__(addr, Type.DEFAULT, timeout)


class DitooPro(Device):
    def __init__(self, addr: str, timeout: int = 1) -> None:
        super().__init__(addr, Type.DITOO_PRO, timeout)

    def show_anim(self, image: DitooProImage) -> None:
        buf = b"".join(image.serialize())

        start_buf = struct.pack("=BHH", 0x0, len(buf), 0)
        cmd = Command(Commands.DITOOPRO_SHOW_ANIM, None, start_buf, False)
        self.send(cmd.command)

        for i, chunk in enumerate(self._chunked(buf, 0x100)):
            data_cmd: bytes = struct.pack(
                f"=BHHH{len(chunk)}s", 0x1, len(buf), 0, i, chunk
            )
            cmd = Command(Commands.DITOOPRO_SHOW_ANIM, None, data_cmd, False)
            self.send(cmd.command, True)

    def _chunked(self, s: bytes, size: int) -> Iterable:
        return (s[i : i + size] for i in range(0, len(s), size))
