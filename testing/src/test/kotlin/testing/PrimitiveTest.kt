package testing

import io.github.stefankoppier.mapping.annotations.Mapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrimitiveTest {

    @Test
    fun `map Person to PersonDto`() {
        assertEquals("2", IntMapper.map(2))
    }
}