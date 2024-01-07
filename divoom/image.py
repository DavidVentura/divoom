import struct
import os
import math
from dataclasses import dataclass, field
from PIL import Image

SIZE = 11


def solid_color(r, g, b):
    assert r < 16 and r >= 0
    assert g < 16 and g >= 0
    assert b < 16 and b >= 0
    return _pack_tuples_to_image([(r, g, b)] * SIZE * SIZE)


def _pack_tuples_to_image(tuples):
    assert len(tuples) == SIZE * SIZE
    ret = []
    for i in range(0, len(tuples) - 1, 2):
        r1, g1, b1 = tuples[i]
        r2, g2, b2 = tuples[i + 1]
        ret.append((g1 << 4) + r1)
        ret.append((r2 << 4) + b1)
        ret.append((b2 << 4) + g2)

    r, g, b = tuples[-1]
    ret.append((g << 4) + r)
    ret.append((0 << 4) + b)  # there's no next pixel
    assert (
        len(ret) == 182
    )  # 12 bits per pixel * 11 * 11 pixels / 8 bits per byte = 181.5
    return ret


def _image_to_bytes(im):
    if im.mode != "RGB":
        im = im.convert("RGB")

    squash = lambda x: min(int(round(x / 16)), 15)  # 255 -> 15
    data = [(squash(r), squash(g), squash(b)) for (r, g, b) in im.getdata()]
    return _pack_tuples_to_image(data)


def image_to_bytes(filename):
    assert os.path.isfile(filename)
    im = Image.open(filename)
    assert im.size == (SIZE, SIZE)
    return _image_to_bytes(im)


class DitooProFrame:
    image: "DitooProImage"
    bitmap: list[int]
    duration: int
    colors: list[tuple[int, int, int]]

    def __init__(self, image: "DitooProImage", duration: int, idx: int = 0):
        self.image = image
        self.duration = duration
        self.colors = []
        self.fill(idx)

    def fill(self, idx: int):
        self.bitmap = [idx] * self.image.size**2

    def rect(self, x: int, y: int, w: int, h: int, idx: int):
        x1 = max(min(x, self.image.size), 0)
        y1 = max(min(y, self.image.size), 0)
        x2 = min(x1 + w, self.image.size)
        y2 = min(y1 + h, self.image.size)

        # Iterate over area and fill
        for i in range(x1, x2):
            for j in range(y1, y2):
                self.bitmap[self.image.size * j + i] = idx

    def img(self, x: int, y: int, w: int, h: int, idx: int, data: list[list[bool]]):
        x1 = max(min(x, self.image.size), 0)
        y1 = max(min(y, self.image.size), 0)
        x2 = min(x1 + w, self.image.size)
        y2 = min(y1 + h, self.image.size)

        # Iterate over area and fill
        for i in range(x1, x2):
            for j in range(y1, y2):
                if data[j - y1][i - x1]:
                    self.bitmap[self.image.size * j + i] = idx

    def serialize(self):
        # Serialize colors
        color_buf = []
        for color in self.colors:
            color_buf.extend(color)

        # Serialize bitmap
        bits = math.ceil(math.log2(self.image.color_bits()))
        bitmap_buf = []
        b = 0
        offset = 0

        for v in self.bitmap:
            b |= v << offset
            offset += bits
            if offset >= 8:
                bitmap_buf.append(b & 0xFF)
                b >>= 8
                offset = offset % 8

        if offset > 0:
            bitmap_buf.append(b)

        color_buf_len = len(color_buf)
        bitmap_buf_len = len(bitmap_buf)

        return struct.pack(
            f"=BHHBB{color_buf_len}s{bitmap_buf_len}s",
            0xAA,
            1 + 2 + 2 + 1 + 1 + color_buf_len + bitmap_buf_len,
            self.duration,
            0x00 if self.colors else 0x01,
            len(self.colors),
            bytes(color_buf),
            bytes(bitmap_buf),
        )


@dataclass
class DitooProImage:
    size: int = 16
    frames: list[list[int]] = field(default_factory=lambda: [])

    def serialize(self):
        return list(map(lambda f: f.serialize(), self.frames))

    def add_frame(self, duration: int = 1000) -> DitooProFrame:
        f = DitooProFrame(self, duration)
        self.frames.append(f)
        return f

    def color_bits(self) -> int:
        return sum((len(f.colors) for f in self.frames))
