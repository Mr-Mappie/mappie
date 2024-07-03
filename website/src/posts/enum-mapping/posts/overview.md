---
title: "Enum Mapping Overview"
summary: "Performing enum mapping."
eleventyNavigation:
  key: Enum Mapping Overview
  parent: Enum Mapping
  order: 11
---

Mappie supports mapping an enum class to another enum class. This can be achieved by implementing a mapper object which
extends from `EnumMappie`.

The mappings of the enum entries are resolved by name. For example, when constructing a mapper for the enum 
classes `Color`
```kotlin
enum class Color { RED, GREEN, BLUE; }
```
and `Colour`
```kotlin
enum class Colour { RED, GREEN, BLUE; }
```
Mappie will resolve all mappings automatically, as the enum classes have identical entries. 

This can be achieved by writing the following enum mapper
```kotlin
object ColorMapper : EnumMappie<Color, Colour>() {
    override fun map(from: Color): Colour = mapping()
}
```
or equivalently without an explicit body
```kotlin
object ColorMapper : EnumMappie<Color, Colour>()
```