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

Mappie can generate mappers for objects and enums. It aims to be easy to use, infer as much implicit mappings as possible, 
have a consistent API, and have the same runtime performance as if one would write the mapping code manually.

The following snippet gives a small overview of the features of Mappie
```kotlin
data class Person(
    val name: String, 
    val age: Int, 
    val address: Address,
)
data class Address(
    val street: String, 
    val zipcode: String,
)
enum class Gender { MALE, FEMALE, NON_BINARY, OTHER }

data class PersonDto(
    val firstname: String, 
    val age: Int, 
    val streetname: String,
)
enum class GenderDto { MALE, FEMALE, OTHER }

object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person) = mapping {
        to::firstname fromProperty from::name
        to::streetname fromProperty from.address::street
    }
    
    private object GenderMapper : EnumMappie<Gender, GenderDto>() {
        override fun map(from: Gender) = mapping {
            GenderDto.OTHER fromEnumEntry Gender.NON_BINARY
        }
    }
}

```