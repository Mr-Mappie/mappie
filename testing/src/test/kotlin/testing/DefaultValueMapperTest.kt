package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DefaultValueMapperTest {

    @Test
    fun `map DefaultValue to DefaultValueDto via DefaultValueMapper`() {
        assertEquals(
            DefaultValueDto("test", 10),
            DefaultValueMapper.map(DefaultValue("test")),
        )
    }
}