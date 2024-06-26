package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntToLongMapperTest {

    @Test
    fun `map Int to Long via IntToLongMapper`() {
        assertEquals(
            LongOutput(1),
            IntToLongMapper.map(IntInput(1)),
        )
    }
}