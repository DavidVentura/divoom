import bluetooth

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
        try:
            recv = self.sock.recv(256)
            # Might receive many replies
            # < ['0x1', '0x8', '0x0', '0x4', '0x59', '0x55', '0x3', '0x4', '0x4b', '0x0', '0x6', '0x3', '0x4', '0x2',
            #    '0x1', '0x6', '0x0', '0x4', '0x32', '0x55', '0xd2', '0x63', '0x3', '0x4', '0x2',
            #    '0x1', '0x8', '0x0', '0x4', '0x59', '0x55', '0x3', '0x4', '0x49', '0x0', '0x4', '0x3', '0x4', '0x2']

            print('<', [hex(c) for c in recv])
            return recv
        except:
            pass

    def __enter__(self):
        self._connect()
        return self

    def __exit__(self, *args):
        print("Disconnecting")
        self._disconnect()
