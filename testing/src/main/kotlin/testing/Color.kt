package testing

import io.github.stefankoppier.mapping.annotations.EnumMapper

enum class Color {
    RED,
    GREEN,
    BLUE,
}

enum class Colour {
    RED,
    GREEN,
    BLUE,
}

object ColorMapper : EnumMapper<Color, Colour>() {
    override fun map(from: Color): Colour = enumMapping()
}