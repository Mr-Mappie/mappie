package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NestedPropertyMapperTest {

    @Test
    fun `map NestedInput to NestedOutput via NestedPropertyMapper`() {
        assertEquals(
            NestedOutput("Test"),
            NestedPropertyMapper.map(NestedInput(NestedInputValue("Test")))
        )
    }
}