package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PersonTest {

    private val mapper = PersonMapper

    @Test
    fun `map Person to PersonDto`() {
        assertEquals(PersonDto("Sjon", "Sjon", 26), mapper.map(Person("Sjon")))
    }
}