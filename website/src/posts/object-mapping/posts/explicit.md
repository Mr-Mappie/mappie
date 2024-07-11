---
title: "Constructing Explicit Mappings"
summary: "How we can construct Explicit Mappings."
eleventyNavigation:
  key: Constructing Explicit Mappings
  parent: Object Mapping
  order: 6
---

Not all classes one wants to map are equivalent. Mappie supports defining explicit mappings for those which cannot
be resolved automatically. This can be done via properties, values, or expressions as described in the coming 
sections.

Suppose we have a data class `Person`, and we have the data class `PersonDto` which has the property `description`
which is not defined in `Person`
```kotlin
data class Person(
    val name: String, 
    val age: Int,
)

data class PersonDto(
    val name: String, 
    val age: Int, 
    val description: String,
)
```
If one would define a mapper without an explicit mapping for `description`, Mappie will give a compile-time error 
stating that the target `description` has no source defined. The target property can be assigned in different ways:
1. mapping via a source property;
2. mapping via a value; or
3. mapping via an expression.

## Mapping via a Source Property
Targets can be set via the operator `fromProperty`. This will set the target to the given source property. 

For example, the following snippet will construct a mapper where `PersonDto.description` is set to `Person.name`
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromProperty from::name
    }
}
```

Sometimes, one wants to map from a source property, but tweak the value, handle nullability, or transform the source in
some other way. See [The Transform Operator](/object-mapping/the-transform-operator/) for some guidelines. It is also
possible to explicitly reuse a different mapper, this is described in [The Via Operator](/object-mapping/the-via-operator/).

## Mapping via a Value
Targets can be set via the operator `fromValue`. This will set the target to the given value.

For example, the following snippet will construct a mapper where `PersonDto.description` is set to `"unknown"`
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromValue "unknown"
    }
}
```


## Mapping via an Expression
Targets can be set via the operator `fromExpression`. This will set the target to the given lambda result. 

The difference between `fromExpression` and `fromValue` is that `fromExpression` will take a lambda
function as a parameter, which takes the original `source` as a parameter. Allowing for more flexibility. 

For example, the following snippet will construct a mapper where `PersonDto.description` is set to 
`"Description: ${from.name}"`.
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromExpression { from -> 
            "Description: ${from.name}" 
        }
    }
}
```

All mappings can be defined using `fromExpression`, but to keep the mappings clean and give Mappie the most information
to suggest improvements to your code, `fromProperty` combined with either `via` or `transform` is preferred.

## Constructor Parameters without a Backing Property
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

## The Target Type Alias 
We can access the target properties via the target type of the mapper. This can clutter the mapping definition when
many explicit mappings are defined. Mappie defines a special `to` property which can be used instead of the target type.

For example, we can use `to` refer to the property `streetname` of `PersonDto`
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        to::streetname fromProperty from.address::street
    }
}
```
where `to::streetname` is equivalent to `PersonDto::streetname`.
