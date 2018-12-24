import divoom.image
import pytest
import struct
from PIL import Image

SIZE = 11
def test_solid_color():
    image = divoom.image.solid_color(0, 0, 0)
    assert len(image) == 182
    for b in image:
        assert b == 0

    with pytest.raises(AssertionError):
        divoom.image.solid_color(255, 0, 0)

    red = 1
    green = 2
    blue = 3
    states = [ (green << 4) + red, (red << 4) + blue, (blue << 4) + green ]
    image = divoom.image.solid_color(red, green, blue)
    assert len(image) == 182
    for i in range(0, len(image)-1):
        assert image[i] == states[i % len(states)]
    assert image[-1] == blue

def test_image_to_bytes():
    byte_value = struct.pack('<3B', 255, 255, 255)
    l = b''.join([byte_value] * SIZE * SIZE)
    image_data = bytes(l)
    image = Image.frombytes('RGB', (SIZE, SIZE), image_data)
    _bytes = divoom.image._image_to_bytes(image)
    assert _bytes == divoom.image.solid_color(0xf, 0xf, 0xf)
