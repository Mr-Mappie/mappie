---
title: "The Via Operator"
summary: "Reusing other mappers using the via operator."
eleventyNavigation:
  key: The Via Operator
  parent: Object Mapping
  order: 8
---

Mappie has support for reusing mappers in other mappers using the `via` operator.

Suppose we have a data class `Person` containing a reference to a data class `Address`
```kotlin
data class Person(
    val name: String, 
    val address: Address,
)

data class Address(
    val street: String,
)
```
and we have the data class `PersonDto` referencing the data class `AddressDto`
```kotlin
data class PersonDto(
    val name: String, 
    val addressDto: AddressDto,
)

data class AddressDto(
    val street: String,
)
```

We start by defining a mapper for `Address` and `AddressDto`
```kotlin
object AddressMapper : ObjectMappie<Address, AddressDto>()
```
we can then reuse `AddressMapper` to construct a mapper for `Person` and `PersonDto` by referencing `AddressMapper` using
the operator `via`
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Address) = mapping {
        to::addressDto fromProperty from::address via AddressMapper
    }
}
```
Do note, that in this case, if `PersonDto::addressDto` was named `PersonDto::address` the mapping does not have to
be defined explicitly. Mappie will construct an implicit mapping using the via operator.

We can also use `via` to map collections. See [List & Sets](/object-mapping/lists-and-sets/).