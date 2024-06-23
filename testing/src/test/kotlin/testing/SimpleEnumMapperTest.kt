package testing

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class SimpleEnumMapperTest {

    @ParameterizedTest(name = "map Status.{0} to StatusDto.{1}")
    @CsvSource("ON, ON", "OFF, OFF")
    fun `map Status to StatusDto via StatusMapper`(status: Status, statusDto: StatusDto) {
        assertEquals(statusDto, StatusMapper.map(status))
    }
}