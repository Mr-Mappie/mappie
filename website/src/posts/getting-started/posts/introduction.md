---
title: "Introduction"
summary: "Welcome to the official documentation of Mappie."
eleventyNavigation:
  key: Introduction
  parent: Getting Started
  order: 1
---

Welcome to the official documentation of Mappie. Mappie is a Kotlin compiler plugin generating object mapping code at 
compile-time with minimal overhead.

Mappie can generate mappers for objects and enums based on implicit- and explicit mappings. It aims to be easy to use,
infer as much implicit mappings as possible, have a consistent API, and have the same runtime performance as if one 
would write the mapping code manually.

The following snippet gives a small overview of the features of Mappie
```kotlin
data class Person(
    val firstname: String, 
    val age: Int, 
    val gender: Gender,
)
enum class Gender { MALE, FEMALE, NON_BINARY, OTHER }

data class PersonDto(
    val name: String, 
    val age: Int, 
    val gender: GenderDto,
)
enum class GenderDto { MALE, FEMALE, OTHER }

object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person) = mapping {
        to::name fromProperty from::firstname
    }
    
    private object GenderMapper : EnumMappie<Gender, GenderDto>() {
        override fun map(from: Gender) = mapping {
            GenderDto.OTHER fromEnumEntry Gender.NON_BINARY
        }
    }
}
```

We can then use `PersonMapper` by calling it's `map`, `mapList` or `mapSet` function
```kotlin
val personDto = PersonMapper.map(Person("Sjon", 58, Gender.MALE))
```