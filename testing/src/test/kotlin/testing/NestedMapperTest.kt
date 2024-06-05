package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NestedMapperTest {

    @Test
    fun `map Thing to ThingDto via ThingMapper`() {
        assertEquals(
            ThingDto(ThangDto("description"), BooleanDto.TRUE),
            ThingMapper.map(Thing(Thang("description"), Boolean.TRUE))
        )
    }
}