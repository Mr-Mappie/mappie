---
title: "Object Mapping Overview"
summary: "Performing object mapping."
eleventyNavigation:
  key: Object Mapping Overview
  parent: Object Mapping
  order: 3
---

Mappie supports creating object mappers via the base class `ObjectMappie`.

Suppose we have a data class `Person`
```kotlin
data class Person(val name: String, val age: Int)
```
and a data class `PersonDto`
```kotlin
data class PersonDto(val name: String, val age: Int)
```
The fields of `Person` match those of `PersonDto`, and as such, not mappings have to be defined, for example
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping()
}
```

## Unresolved properties
Not all data classes you want to map are equivalent. Suppose the target class `PersonDto` has the property `description`, which is not defined in `Person`
```kotlin
data class PersonDto(
    val name: String, 
    val age: Int, 
    val description: String,
)
```

Mappie will throw an error stating that the target `description` has no source defined.

This can be addressed in multiple ways. 

### Explicit property mapping via another property
A possibility is to map `description` from another property, e.g. via `name`

```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description mappedFromProperty Person::name
    }
}
```

### Explicit property mapping via an expression
A possibility is to map `description` from an expression, e.g. setting it to the constant `"unknown"`

```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description mappedFromExpression { source -> "unknown" }
    }
}
```
the parameter of the lambda `source` is equal to `from`. It does not have to be named.

## Configuration
