package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SetMapperTest {

    @Test
    fun `map BookSet to BookSetDto via BookSetMapper`() {
        assertEquals(
            BookSetDto(setOf("lorem", "ipsum")),
            BookSetMapper.map(BookSet(setOf(Page("lorem"), Page("ipsum")))),
        )
    }
}