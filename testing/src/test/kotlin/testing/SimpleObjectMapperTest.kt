package testing

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleObjectMapperTest {

    @Test
    fun `map StringValue to StringValueDto via StringValueMapper`() {
        assertEquals(
            StringValueDto("Test"),
            StringValueMapper.map(StringValue("Test")),
        )
    }
}