package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListToNullableListMapperTest {

    @Test
    fun `map List to nullable List via NullableListFromList`() {
        assertEquals(
            TargetList(listOf("A", "B")),
            NullableListFromListMapper.map(SourceList(listOf("A", "B"))),
        )
    }
}