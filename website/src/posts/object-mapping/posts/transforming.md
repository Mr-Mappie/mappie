---
title: "Transforming"
summary: "Transforming source properties."
eleventyNavigation:
  key: Transforming
  parent: Object Mapping
  order: 6
---

Mappie can transform source parameters using the operator `transform`. This is useful when we want to change the source
value.

Suppose we have the data class `Person`
```kotlin
data class Person(val name: String, val dateOfBirth: LocalDate)
```
having a date of birth `dateOfBirth` and we have the data class `PersonDto` which has the property `age`
```kotlin
data class PersonDto(
    val name: String, 
    val age: DateTimePeriod, 
)
```
we can create a mapper between `Person` and `PersonDto` via
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::age fromProperty Person::dateOfBirth transform { dateOfBirth ->
            Clock.todayIn(TimeZone.currentSystemDefault()).periodUntil(dateOfBirth) 
        }
    }
}
```

## Handling nullability
The `transform` operator can also come in handy when mapping nullable values to non-nullable values. 

Suppose we have the data class `Dog` having the property `name` which can be `null`
```kotlin
data class Dog(val name: String?)
```
and we have the data class `DogDto` for which the constructor parameter `name` is non-nullable.
```kotlin
data class DogDto(val name: String)
```

We create a mapping between `Dog` and `DogDto` via 
```kotlin
object DogMapper : ObjectMappie<Dog, DogDto>() {
    override fun map(from: Dog): DogDto = mapping {
        DogDto::name fromProperty DogDto::name transform {
            it ?: "unknown"
        }
    }
}
```
which will set the target `DogDto.name` to `"unknown"` if `Dog.name` is `null`.