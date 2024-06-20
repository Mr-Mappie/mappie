package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IdentityMappieTest {

    @Test
    fun `map Int to Int via IdentityMapper`() {
        assertEquals(1, IdentityMapper.map(1))
    }
}