package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntToLongMapperTest {

    @Test
    fun `map Int to Long via IntToLongMapper`() {
        assertEquals(
            IntOutput(1),
            IntToLongMapper.map(LongInput(1)),
        )
    }
}