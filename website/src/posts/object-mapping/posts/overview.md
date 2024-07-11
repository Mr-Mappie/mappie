---
title: "Object Mapping Overview"
summary: "Performing object mapping."
eleventyNavigation:
  key: Object Mapping Overview
  parent: Object Mapping
  order: 4
---

Mappie supports creating object mappers via the base class `ObjectMappie`. Extending this class will instruct Mappie
to generate a mapper which will call a constructor of the target type, based on the explicit mappings, and the
properties of the source type.

Mappie resolves implicit mappings by name and type. Mappie automatically resolves a mapping when both 
properties have the same name, and the target property is assignable from the source property. 

When the source type has a property that matches the name of a target of the target value,
it will be inferred automatically. This requires that the types of the source property and target property match. If 
they do not match, it will be inferred automatically if there exists a single different mapper that takes the source- 
and target types as input- and output types.

Suppose we have a data class `Person`
```kotlin
data class Person(val name: String, val age: Int)
```
and a data class `PersonDto`
```kotlin
data class PersonDto(val name: String, val age: Int)
```
The fields of `Person` match the parameters of the primary constructor of `PersonDto`, and as such, no mappings have 
to be defined. This can be expressed by writing the following mapper
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>()
```
which generates a mapping function calling the primary constructor of `PersonDto` with the fields of `Person`.
