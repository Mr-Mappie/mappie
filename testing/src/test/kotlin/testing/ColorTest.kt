package testing

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class ColorTest {

    private val mapper = ColorMapper

    @ParameterizedTest(name = "map Color.{0} to Colour.{1}")
    @CsvSource("RED, RED", "BLUE, BLUE", "GREEN, GREEN")
    fun `map Color to Colour`(color: Color, colour: Colour) {
        assertEquals(colour, mapper.map(color))
    }
}