package testing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListMapperTest {

    @Test
    fun `map BookList to BookListDto via BookMapper`() {
        assertEquals(
            BookListDto(listOf("lorem", "ipsum")),
            BookListMapper.map(BookList(listOf(Page("lorem"), Page("ipsum")))),
        )
    }
}