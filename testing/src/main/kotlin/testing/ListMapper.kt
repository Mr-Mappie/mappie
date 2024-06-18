package testing

import io.github.mappie.api.ObjectMapper

data class Book(val pages: List<Page>)

data class Page(val text: String)

data class BookDto(val pages: List<String>)

object BookMapper : ObjectMapper<Book, BookDto>() {
    override fun map(from: Book): BookDto = mapping {
        BookDto::pages mappedFromProperty Book::pages via PageMapper.forList
    }
}

object PageMapper : ObjectMapper<Page, String>() {
    override fun map(from: Page): String = from.text
}