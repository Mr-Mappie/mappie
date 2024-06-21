package testing

import io.github.mappie.api.EnumMappie

enum class Color {
    RED,
    GREEN,
    BLUE,
    ORANGE,
    PURPLE,
}

enum class Colour {
    RED,
    GREEN,
    BLUE,
    OTHER,
}

object ColorMapper : EnumMappie<Color, Colour>() {
    override fun map(from: Color): Colour = mapping {
        Colour.OTHER fromEnumEntry Color.ORANGE
        Colour.OTHER fromEnumEntry Color.PURPLE
    }
}