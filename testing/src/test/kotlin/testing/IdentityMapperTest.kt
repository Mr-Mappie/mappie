package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IdentityMapperTest {

    @Test
    fun `map Int to Int via IdentityMapper`() {
        assertEquals(1, IdentityMapper.map(1))
    }
}