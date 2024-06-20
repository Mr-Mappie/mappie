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
        Colour.OTHER mappedFromEnumEntry Color.ORANGE
        Colour.OTHER mappedFromEnumEntry Color.PURPLE
    }
}