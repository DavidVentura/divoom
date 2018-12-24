import struct
import os
from PIL import Image

SIZE = 11
def solid_color(r, g, b):
    assert r < 16 and r >= 0
    assert g < 16 and g >= 0
    assert b < 16 and b >= 0
    return _pack_tuples_to_image([ (r, g, b) ] * SIZE * SIZE)

def _pack_tuples_to_image(tuples):
    assert len(tuples) == SIZE * SIZE
    ret = []
    for i in range(0, len(tuples)-1, 2):
        r1, g1, b1 = tuples[i]
        r2, g2, b2 = tuples[i+1]
        ret.append((g1 << 4) + r1)
        ret.append((r2 << 4) + b1)
        ret.append((b2 << 4) + g2)

    r, g, b = tuples[-1]
    ret.append((g << 4) + r)
    ret.append((0 << 4) + b) # there's no next pixel
    assert len(ret) == 182 # 12 bits per pixel * 11 * 11 pixels / 8 bits per byte = 181.5
    return ret

def image_to_bytes(filename):
    assert os.path.isfile(filename)
    im = Image.open(filename)
    assert im.size == (SIZE, SIZE)
    if im.mode != 'RGB':
        im = im.convert('RGB')

    squash = lambda x: min(int(round(x/16)), 15) # 255 -> 15
    data = [(squash(r), squash(g), squash(b)) for (r, g, b) in im.getdata()]
    return _pack_tuples_to_image(data)
