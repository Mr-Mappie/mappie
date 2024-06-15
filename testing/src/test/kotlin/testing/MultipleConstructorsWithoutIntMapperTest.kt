package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MultipleConstructorsWithoutIntMapperTest {

    @Test
    fun `map MultipleConstructors to MultipleConstructorsDto via MultipleConstructorsWithoutIntMapper calls secondary constructor`() {
        assertEquals(
            MultipleConstructorsDto("test", 1),
            MultipleConstructorsWithoutIntMapper.map(MultipleConstructors("test")),
        )
    }

    @Test
    fun `map MultipleConstructors to MultipleConstructorsDto via MultipleConstructorsWithoutIntMapper calls primary constructor`() {
        assertEquals(
            MultipleConstructorsDto("test", 2),
            MultipleConstructorsWitIntMapper.map(MultipleConstructors("test")),
        )
    }
}