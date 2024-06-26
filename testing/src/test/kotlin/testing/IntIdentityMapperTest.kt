package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntIdentityMapperTest {

    @Test
    fun `map Int to Int via IdentityMapper`() {
        assertEquals(1, IntIdentityMapper.map(1))
    }
}