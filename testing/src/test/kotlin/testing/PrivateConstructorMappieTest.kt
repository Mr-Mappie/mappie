package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrivateConstructorMappieTest {

    @Test
    fun `map PrivateConstructor to PrivateConstructorDto via PrivateConstructorMapper`() {
        assertEquals(
            PrivateConstructorDto("test", 1),
            PrivateConstructorMapper.map(PrivateConstructor("test")),
        )
    }

}