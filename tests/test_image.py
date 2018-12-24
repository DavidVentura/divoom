import divoom.image
import pytest

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

test_solid_color()
