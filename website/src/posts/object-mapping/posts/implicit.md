---
title: "Inferring Implicit Mappings"
summary: "How Mappie infers Implicit Mappings."
eleventyNavigation:
  key: Inferring Implicit Mappings
  parent: Object Mapping
  order: 5
---

Mappie infers implicit mappings by name, type, default arguments, getter- and setter methods, and other mappers that 
are defined. An implicit mapping for a target property is inferred automatically if it has the same name as a source
property, and it is assignable from that source property. If it is not assignable, Mappie will check if there is a 
single mapper defined that can map the source type to the target type, and will automatically apply it.

For example, suppose we have a data class `Person` and a data class `PersonDto`
```kotlin
data class Person(val name: String, val age: Int)

data class PersonDto(val name: String, val age: Int)
```
The properties of `Person` match the parameters of the primary constructor of `PersonDto`, and as such, no explicit 
mappings have to be defined. We can simply construct such a mapper by writing
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>()
```
which will generate a mapper which calls the primary constructor of `PersonDto` assigned to the fields of `Person`.

## Mapper Generation
Mappie can also generates mappers automatically. When a source type and a target type do not have an existing mapper,
and one can be written without any explicit mappings, it will be generated automatically. 

For example, suppose we have the data classes `Person` and `PersonDto` containing `Gender` and `GenderDto` enum classes
```kotlin
data class Person(val name: String, val gender: Gender)
enum class Gender { MALE, FEMALE, OTHER; }

data class PersonDto(val name: String, val gender: GenderDto)
enum class GenderDto { MALE, FEMALE, OTHER; }
```
We can generate a mapper from `Person` to `PersonDto` by writing
```kotlin
class PersonMapper : ObjectMappie<Person, PersonDto>()
```
and the nested mapper from `Gender` to `GenderDto` will be generated automatically as they both contain the same enum
entries.

Note that at this point, this is implemented only for enum classes. Class mappers is work in progress and will be added 
soon.

## Default Arguments
Mappie also considers default arguments as a possibility.

For example, suppose `PersonDto` is defined as
```kotlin
data class PersonDto(
    val name: String, 
    val age: Int, 
    val hasChildren: Boolean = false,
)
```
Mappie will use the default argument `false` for `hasChildren` if no explicit mapping is defined. This is enabled by
default and can be disabled by setting the configuration option `useDefaultArguments` to `false`. 