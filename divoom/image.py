import struct
def solid_color(r, g, b):
    SIZE = 11
    ret = []
    assert r < 16 and r >= 0
    assert g < 16 and g >= 0
    assert b < 16 and b >= 0

    #rgb = 12 bits
    _b = None
    state = 'new'
    for y in range(0, SIZE):
        for x in range(0, SIZE):
            if state == 'new':
                _b = (g << 4) + r
                ret.append(_b)
                _b = b
                state = 'started'
            elif state == 'started':
                _b += (r << 4)
                ret.append(_b)
                _b = (b << 4) + g
                ret.append(_b)
                _b = None
                state = 'new'

    if _b is not None:
        ret.append(b)
    assert len(ret) == 182 # 12 bits per pixel * 11 * 11 pixels / 8 bits per byte = 181.5
    return ret
