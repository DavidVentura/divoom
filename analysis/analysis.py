import pyshark
from divoom.protocol import valid_reply, valid_command, parse_reply, Command, Commands
from divoom.device import HELLO_BYTES

def to_bytes(_str):
    return [ int(h, 16) for h in _str.split(':') ]

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



cap = pyshark.FileCapture('btsnoop_hci.log', display_filter='btspp')
for p in cap:
    b = to_bytes(p.btspp.data)
    if b == HELLO_BYTES:
        continue
    if not valid_command(b):
        print('Invalid!!', b)
        continue
    if valid_reply(b):
        print(b)
        print('>', parse_reply(b))
        print('---')
    else: # COMMAND
        print('COMM', [hex(_b) for _b in b])
        if b[3] in (0, 0x57, 0x58, 0x5d, 0xb6):
            continue
        print('!!')
        # print(from_bytes(b))

