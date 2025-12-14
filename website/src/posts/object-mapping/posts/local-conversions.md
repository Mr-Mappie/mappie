---
title: "Local Conversion Methods"
summary: "Defining reusable type conversion methods inside mappers."
since: 2.2.0
eleventyNavigation:
  key: Local Conversion Methods
  parent: Object Mapping
  order: 9
---

Mappie supports defining local conversion methods inside your mapper classes that are automatically discovered and used
when type conversion is needed. This provides a simpler alternative to creating separate mapper classes for simple type
conversions.

## Basic Usage

Suppose we have a wrapper type `StringWrapper` and want to map it to a `String`:
```kotlin
data class StringWrapper(val value: String)

data class Person(val name: StringWrapper)
data class PersonDto(val name: String)
```

Instead of creating a separate `ObjectMappie<StringWrapper, String>`, you can define a conversion method directly in
your mapper:
```kotlin
object PersonMapper : ObjectMappie<Person, PersonDto>() {

    fun unwrap(wrapper: StringWrapper): String = wrapper.value

    override fun map(from: Person) = mapping()
}
```

Mappie will automatically discover the `unwrap` method and use it when it needs to convert `StringWrapper` to `String`.

## Method Requirements

A method is recognized as a local conversion method if it:
- Takes exactly one parameter
- Returns a type different from the parameter type
- Is not one of Mappie's own methods (`map`, `mapNullable`, `mapList`, etc.)

The method can have any name - it doesn't need to be called `map`.

## Reusable Conversions via Interfaces

For maximum reuse, you can define conversion methods in an interface and implement it in multiple mappers:
```kotlin
interface CommonConverters {
    fun unwrap(wrapper: StringWrapper): String = wrapper.value
    fun unwrapInt(wrapper: IntWrapper): Int = wrapper.value
}

object PersonMapper : ObjectMappie<Person, PersonDto>(), CommonConverters {
    override fun map(from: Person) = mapping()
}

object EmployeeMapper : ObjectMappie<Employee, EmployeeDto>(), CommonConverters {
    override fun map(from: Employee) = mapping()
}
```

Both mappers will automatically use the conversion methods defined in `CommonConverters`.

## Generic Conversion Methods

Local conversion methods can also be generic:
```kotlin
data class Input(val items: List<String>)
data class Output(val items: Set<String>)

object Mapper : ObjectMappie<Input, Output>() {

    fun <T> toSet(list: List<T>): Set<T> = list.toSet()

    override fun map(from: Input) = mapping()
}
```

## Excluding Methods

If you have a method that matches the conversion method signature but should not be used for automatic conversion,
annotate it with `@ExcludeFromMapping`:
```kotlin
import tech.mappie.api.config.ExcludeFromMapping

object PersonMapper : ObjectMappie<Person, PersonDto>() {

    @ExcludeFromMapping
    fun validateWrapper(wrapper: StringWrapper): String {
        require(wrapper.value.isNotBlank())
        return wrapper.value
    }

    override fun map(from: Person) = mapping {
        PersonDto::name fromProperty Person::name transform { it.value }
    }
}
```

This is useful when you have utility methods that happen to take one parameter and return a different type, but should
not be used as automatic conversion methods.

## Priority

When multiple conversion options are available, Mappie uses the following priority order:
1. Local conversion methods defined in the mapper class itself
2. Conversion methods inherited from interfaces
3. Other `ObjectMappie` mappers (internal or external)
4. Built-in mappers

This means local conversion methods will take precedence over built-in mappers, allowing you to override default
behavior when needed.

## Comparison with Via Operator

Local conversion methods are ideal for:
- Simple type unwrapping/wrapping
- Conversions that don't require the full power of `ObjectMappie`
- Reusable conversions shared across multiple mappers via interfaces

The [via operator](/object-mapping/the-via-operator/) is better suited for:
- Complex object-to-object mappings
- Cases where you need explicit control over which mapper to use
- Nested object mappings with their own mapping logic
