package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PersonMapperTest {

    @Test
    fun `map Person to PersonDto via PersonMapper`() {
        assertEquals(
            PersonDto("Sjon", "Sjon", 26),
            PersonMapper.map(Person("Sjon"))
        )
    }

    @Test
    fun `map Person to PersonDto via ConstructorCallPersonMapper`() {
        assertEquals(
            PersonDto("Cindy", "description", 10),
            ConstructorCallPersonMapper.map(Person("Cindy"))
        )
    }

    @Test
    fun `map Person to PersonDto via TransformingPersonMapper`() {
        assertEquals(
            PersonDto("Firstname", "Firstname Surname", 24),
            TransformingPersonMapper.map(Person("Firstname"))
        )
    }
}