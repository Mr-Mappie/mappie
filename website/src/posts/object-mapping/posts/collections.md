---
title: "Lists & Sets"
summary: "Resolving source- and target properties."
eleventyNavigation:
  key: Lists & Sets
  parent: Object Mapping
  order: 10
---

Mappie has extensive support for collection types. 

An `ObjectMappie` defines the functions `mapList`, `mapSet`, 
`mapArray` (JVM only), and nullable variants `mapNullableList`, `mapNullableSet` to map collections directly.

For example, we can use `mapList` to automatically map a `List`
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>()

val persons: List<Person> = listOf(Person("Sjon"), Person("Piet"))
val personDtos: List<PersonDto> = PersonMapper.mapList(persons)
```

Mappie also defines several built-in mappers to map collections, most notable for `List` and `Set`. These built-in 
mappers are used to automatically map collection types. When defining a mapping manually, these mappers can be 
referenced explicitly; they are defined in the package `mappie.api.builtin.collections`.

For example, suppose we have the data class `Book` containing a list of `Page`:
```kotlin
data class Book(val pages: List<Page>)

data class Page(val text: String)
```
and we have a data class `BookDto` containing a list of strings
```kotlin
data class BookDto(val pages: List<String>)
```

We can define a mapping between `Book` and `BookDto` by defining two mappers: a mapper for `Page` to `String`, which simply
gets the `text` property, and a mapper between `Book` and `BookDto` which uses the built-in mapper `IterableToListMapper`:
```kotlin
object PageMapper : ObjectMappie<Page, String>() {
    override fun map(from: Page): String = from.text
}

object BookMapper : ObjectMappie<Book, BookDto>() {
    override fun map(from: Book): BookDto = mapping {
        BookDto::pages fromProperty Book::pages via IterableToListMapper(PageMapper)
    }
}
```
Note that in this case `BookMapper` is superfluous and is equivalent to 
```kotlin
object BookMapper : ObjectMappie<Book, BookDto>() {
    override fun map(from: Book): BookDto = mapping {
        BookDto::pages fromProperty Book::pages
    }
}
```
or even 
```kotlin
object BookMapper : ObjectMappie<Book, BookDto>()
```