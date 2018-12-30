import bluetooth
from divoom.protocol import parse_reply, split_reply

HELLO_BYTES = [0x0, 0x5, 0x48, 0x45, 0x4c, 0x4c, 0x4f, 0x0]
class Device:
    def __init__(self, addr, timeout=1):
        self.sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.addr = addr
        self.timeout = timeout

    def _connect(self):
        self.sock.connect((self.addr, 4))
        hello = list(self.sock.recv(256))
        assert hello == HELLO_BYTES

    def _disconnect(self):
        self.sock.close()

    def send(self, package, raw=False):
        self.sock.send(bytes(package))
        print('>', [hex(c) for c in package])
        if raw == False:
            return self.get_message()

    def get_message(self):
        recv = self.sock.recv(256) # Might receive many replies
        print('<', [hex(c) for c in recv])
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
