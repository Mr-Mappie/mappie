package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MultipleConstructorsMapperTest {

    @Test
    fun `map MultipleConstructors to MultipleConstructorsDto via MultipleConstructorsMapper`() {
        assertEquals(
            MultipleConstructorsDto("test", 1),
            MultipleConstructorsMapper.map(MultipleConstructors("test")),
        )
    }

}