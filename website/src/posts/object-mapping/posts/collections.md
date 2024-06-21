---
title: "Lists & Sets"
summary: "Resolving source- and target properties."
eleventyNavigation:
  key: Lists & Sets
  parent: Object Mapping
  order: 7
---

All mappers of Mappie define mapper variants for collections. Specifically, for `List` and `Set`. When we want to map a 
property of such a type, we do not want to copy the collection itself, but the elements contained in such a type. We can 
reference a mapper using the getters `forList` and `forSet` defined in each mapper.

Suppose we have the data class `Book` containing a list of `Page`
```kotlin
data class Book(val pages: List<Page>)

data class Page(val text: String)
```
and we have a data class `BookDto` containing a list of strings
```kotlin
data class BookDto(val pages: List<String>)
```

We can defined a mapping between `Book` and `BookDto` by defining two mappers: a mapper for `Page` to `String`, which simply
gets the `text` property, and mapper between `Book` and `BookDto` using the inner `forList` mapper of the `PageMapper`
```kotlin
object PageMapper : ObjectMappie<Page, String>() {
    override fun map(from: Page): String = from.text
}

object BookMapper : ObjectMappie<Book, BookDto>() {
    override fun map(from: Book): BookDto = mapping {
        BookDto::pages mappedFromProperty Book::pages via PageMapper.forList
    }
}
```