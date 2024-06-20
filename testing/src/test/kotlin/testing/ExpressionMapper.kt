package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExpressionMappieTest {

    @Test
    fun `map Person to PersonDto via ExpressionMapper`() {
        assertEquals(
            PersonDto("Name", Person::class.simpleName!!, 10),
            ExpressionMapper.map(Person("Name"))
        )
    }
}