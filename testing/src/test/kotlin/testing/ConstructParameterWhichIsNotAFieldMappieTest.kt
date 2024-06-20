package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConstructParameterWhichIsNotAFieldMappieTest {

    @Test
    fun `map ConstructorParameterWhichIsNotAField to ConstructorParameterWhichIsNotAFieldDto via ConstructorParameterWhichIsNotAFieldMapper`() {
        assertEquals(
            ConstructorParameterWhichIsNotAFieldDto("test"),
            ConstructorParameterWhichIsNotAFieldMapper.map(ConstructorParameterWhichIsNotAField("test")),
        )
    }

}