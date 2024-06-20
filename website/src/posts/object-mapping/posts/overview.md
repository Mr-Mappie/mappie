---
title: "Object Mapping Overview"
summary: "Performing object mapping."
eleventyNavigation:
  key: Object Mapping Overview
  parent: Object Mapping
  order: 3
---

Mappie supports creating object mappers via the base class `ObjectMappie`. The generated mapper will call a constructor
of the target type, based on the properties and explicit mappings from the source type.

Suppose we have a data class `Person`
```kotlin
data class Person(val name: String, val age: Int)
```
and a data class `PersonDto`
```kotlin
data class PersonDto(val name: String, val age: Int)
```
The fields of `Person` match the parameters of the primary constructor of `PersonDto`, and as such, no mappings have 
to be defined. For example
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping()
}
```
will generate a mapping function calling the primary constructor of `PersonDto` with the fields of `Person`.
