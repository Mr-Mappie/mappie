---
title: "Resolving"
summary: "Resolving source- and target properties."
eleventyNavigation:
  key: Resolving
  parent: Object Mapping
  order: 5
---

Not all data classes you want to map are equivalent. Suppose we have a data class `Person`
```kotlin
data class Person(val name: String, val age: Int)
```
and we have the data class `PersonDto` which has the property `description`, which is not defined in `Person`
```kotlin
data class PersonDto(
    val name: String, 
    val age: Int, 
    val description: String,
)
```

When a mapping function is defined without any explicit mappings, Mappie will throw an error stating that the target 
`description` has no source defined. We can set the target property in different ways:
1. setting the target from a source property;
2. setting the target from a constant; or
3. setting the target from an expression.

## Mapping via a Source Property
Targets can be set via the operator `fromProperty`. This will set the target to the given source property.
For example
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromProperty Person::name
    }
}
```
will set `PersonDto.description` to `Person.name`.

We can also select a nested property using `fromProperty`. This can be done by selecting the property from the mapping
parameter, usually named `from`. For example
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::streetname fromProperty from.address::street
    }
}
```
will construct an explicit mapping from the property `street` from the `address` of `from` to `streetname` of `PersonDto`. 

Sometimes, you want to map from a source property, but tweak the value, handle nullability, or transform the source in
some other way. See [Transforming](/object-mapping/transforming/) for some guidelines.

## Mapping via a Value
Targets can be set via the operator `fromValue`. This will set the target to the given value.
For example
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromValue "unknown"
    }
}
```
will always set `PersonDto.description` to `"unknown"`.

## Mapping via an Expression
Targets can be set via the operator `fromExpression`. This will set the target to the given lambda result. 

The difference between `fromExpression` and `fromValue` is that `fromExpression` will take a lambda
function as a parameter, which takes the original `source` as a parameter. Allowing for more flexibility. 

For example
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromExpression { from -> "Description: ${from.name}" }
    }
}
```
will set `PersonDto.description` to `"Description: ${from.name}"`.

## Targeting a constructor parameter without a backing property
It is possible that a constructor parameter is declared without a backing property. We can handle those constructor 
parameters via the function `parameter`.

For example, suppose that we use the same example as above, but `PersonDto.description` does not declare a backing property.
```kotlin
data class PersonDto(
    val name: String,
    val age: Int,
    description: String,
)
```
We cannot reference `description` via a property reference `PersonDto::description`. To target the constructor parameter, 
we can use `parameter("description")` to reference the constructor parameter
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        parameter("description") fromValue "a constant"
    }
}
```