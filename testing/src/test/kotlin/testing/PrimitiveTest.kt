package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrimitiveTest {

    @Test
    fun `map Int to String via IntMapper`() {
        assertEquals("2", IntMapper.map(2))
    }

    @Test
    fun `map String to Int via StringMapper`() {
        assertEquals(2, StringMapper.map("2"))
    }
}