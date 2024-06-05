package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BookMapperTest {

    @Test
    fun `map Book to BookDto via BookMapper`() {
        assertEquals(
            BookDto(listOf("lorem", "ipsum")),
            BookMapper.map(Book(listOf(Page("lorem"), Page("ipsum")))),
        )
    }
}