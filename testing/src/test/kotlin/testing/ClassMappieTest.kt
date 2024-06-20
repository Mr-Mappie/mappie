package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ClassMappieTest {

    @Test
    fun `map Class to ClassDto via ClassMapper`() {
        assertEquals(
            ClassDto("test", 1),
            ClassMapper.map(Class("test", 100)),
        )
    }
}