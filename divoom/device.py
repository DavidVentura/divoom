import bluetooth
import logging
from enum import Enum
from divoom.protocol import parse_reply, split_reply

logger = logging.getLogger(__name__)


class Type(Enum):
    DEFAULT = 0
    DITOO_PRO = 1


CONFIG_MAP = {
    # port, hello bytes, size
    Type.DEFAULT: (4, [0x0, 0x5, 0x48, 0x45, 0x4C, 0x4C, 0x4F, 0x0], 11),
    Type.DITOO_PRO: (2, None, 16),
}


class Device:
    def __init__(self, addr, type_: Type = Type.DEFAULT, timeout=1):
        self.sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.addr = addr
        self.timeout = timeout
        self.type_ = type_

    def _connect(self):
        port, hello_bytes, _ = CONFIG_MAP[self.type_]
        self.sock.connect((self.addr, port))

        if hello_bytes is not None:
            resp = list(self.sock.recv(256))
            assert resp == hello_bytes

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
