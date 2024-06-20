package testing

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class ColorMappieTest {

    @ParameterizedTest(name = "map Color.{0} to Colour.{1}")
    @CsvSource("RED, RED", "BLUE, BLUE", "GREEN, GREEN", "ORANGE, OTHER", "PURPLE, OTHER")
    fun `map Color to Colour via ColorMapper`(color: Color, colour: Colour) {
        assertEquals(colour, ColorMapper.map(color))
    }
}