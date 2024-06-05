package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NestedMapperTest {

    @Test
    fun `map Thing to ThingDto via ThingMapper`() {
        assertEquals(
            ThingDto(ThongDto("description")),
            ThingMapper.map(Thing(Thong("description")))
        )
    }
}