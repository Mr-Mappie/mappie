---
title: "Enum Mapping Overview"
summary: "Performing enum mapping."
eleventyNavigation:
  key: Enum Mapping Overview
  parent: Enum Mapping
  order: 14
---

Mappie supports mapping an enum class to another class. This can be achieved by implementing a mapper which extends 
from `EnumMappie`. If the target type of the mapper is an enum class, the mappings of the enum entries are resolved by 
name. If the target type is not an enum class, no implicit mappings can be constructed and all mappings must be defined 
explicitly.

For example, when constructing a mapper for the enum classes `Color` and `Colour`
```kotlin
enum class Color { RED, GREEN, BLUE; }

enum class Colour { RED, GREEN, BLUE; }
```
Mappie will resolve all mappings automatically, as the enum classes have identical entries. This can be achieved by 
writing the following enum mapper
```kotlin
object ColorMapper : EnumMappie<Color, Colour>()
```