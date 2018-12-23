import bluetooth
from divoom.protocol import parse_reply, split_reply

class Device:
    def __init__(self, addr):
        self.sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.addr = addr

    def _connect(self):
        self.sock.connect((self.addr, 4))
        self.sock.settimeout(1)
        hello = list(self.sock.recv(256))
        assert hello == [0x0, 0x5, 0x48, 0x45, 0x4c, 0x4c, 0x4f, 0x0]

    def _disconnect(self):
        self.sock.close()

    def send(self, package):
        self.sock.send(bytes(package))
        print('>', [hex(c) for c in package])
        return self.get_message()

    def get_message(self):
        try:
            recv = self.sock.recv(256) # Might receive many replies

            print('<', [hex(c) for c in recv])
            return self.__parse_reply(recv)
        except bluetooth.btcommon.BluetoothError:
            pass

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
