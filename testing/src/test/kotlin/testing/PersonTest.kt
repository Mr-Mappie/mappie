package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PersonTest {

    private val mapper = PersonMapper

    @Test
    fun `map Person to PersonDto`() {
        assertEquals(mapper.map(Person("Sjon", 42)), PersonDto("Sjon", 42))
    }
}