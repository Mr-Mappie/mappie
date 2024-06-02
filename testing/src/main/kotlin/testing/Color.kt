package testing

import io.github.stefankoppier.mapping.annotations.EnumMapper

enum class Color {
    RED,
    GREEN,
    BLUE,
    ORANGE,
}

enum class Colour {
    RED,
    GREEN,
    BLUE,
    OTHER,
}

object ColorMapper : EnumMapper<Color, Colour>() {
    override fun map(from: Color): Colour = enumMapping()
}