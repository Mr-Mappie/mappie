---
title: "Reusing Mappers"
summary: "Reusing other mappers."
eleventyNavigation:
  key: Reusing Mappers
  parent: Object Mapping
  order: 7
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
    val address: AddressDto,
)

data class AddressDto(
    val street: String,
)
```

We start by defining a mapper for `Address` and `AddressDto`
```kotlin
object AddressMapper : ObjectMappie<Address, AddressDto>() {
    override fun map(from: Address) = mapping()
}
```
we can then reuse `AddressMapper` to construct a mapper for `Person` and `PersonDto` by referencing `AddressMapper` using
the operator `via`
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Address) = mapping {
        PersonDto::address fromProperty PersonDto::address via AddressMapper
    }
}
```

We can also use `via` to map collections. See [List & Sets](/object-mapping/lists-and-sets/).