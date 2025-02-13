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

The target type is not always assignable from the source type. There are several ways to handle this. One way is to
define a mapper from the source type to the target type. This can be applied explicitly using [The Via Operator](/object-mapping/the-via-operator/), 
or be implicitly applied by Mappie. 

It is also possible to transform the property. For example to tweak the value, handle nullability, or transform the 
source in some other way. See [The Transform Operator](/object-mapping/the-transform-operator/) for some guidelines.

### Nullability

When mapping from a nullable type to a non-nullable type, one has several options. The most flexible option is to
use the transform operator. 

When the transformation logic is applying a simple non-null assertion operator, or a 
`requireNotNull` function call, `to::x fromPropertyNotNull from::y` steps in as an equivalent alternative to 
```kotlin 
to::x fromProperty from::y transform { it!! }
```

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

## Handling non-referenceable Targets
We can use the `to` function to refer to constructor parameters which do not have a property or to refer to a setter
method.

For example, suppose that we use the same example as above, but `PersonDto.description` does not declare a backing property.
```kotlin
data class PersonDto(
    val name: String,
    val age: Int,
    description: String,
)
```
We cannot reference `description` via a property reference `Person::description`. To target the constructor parameter,
we can use `to("description")` to reference the constructor parameter
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        to("description") fromValue "a constant"
    }
}
```

## The to Alias
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
