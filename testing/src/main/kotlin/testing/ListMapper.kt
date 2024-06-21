package testing

import io.github.mappie.api.ObjectMappie

data class BookList(val pages: List<Page>)

data class BookSet(val pages: Set<Page>)

data class Page(val text: String)

data class BookListDto(val pages: List<String>)

data class BookSetDto(val pages: Set<String>)

object BookListMapper : ObjectMappie<BookList, BookListDto>() {
    override fun map(from: BookList): BookListDto = mapping {
        BookListDto::pages fromProperty BookList::pages via PageMapper.forList
    }
}

object BookSetMapper : ObjectMappie<BookSet, BookSetDto>() {
    override fun map(from: BookSet): BookSetDto = mapping {
        BookSetDto::pages fromProperty BookSet::pages via PageMapper.forSet
    }
}

object PageMapper : ObjectMappie<Page, String>() {
    override fun map(from: Page): String = from.text
}