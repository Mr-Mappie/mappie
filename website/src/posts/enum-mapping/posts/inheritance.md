---
title: "Inheriting Mappers"
summary: "Re-using functionality using inheritance."
since: 2.2.0
eleventyNavigation:
  key: Inheriting Mappers
  parent: Enum Mapping
  order: 16
---

We can use inheritance to share logic of enum mapping in `EnumMappie`. This can be in two ways: by calling the
logic of a superclass, or by combining the logic of the superclass with that of the subclass.

We might want to call the logic of the superclass when we have different enums that require the same strategy. 
For example, suppose we have two source enums:
```kotlin
enum class Gender { Male, Female, Other }
enum class EyeColor { Blue, Green, Other }
```
and their respective target enums:
```kotlin
enum class GenderDto { MALE, FEMALE, OTHER }
enum class EyeColorDto { BLUE, GREEN, OTHER }
```

We can define an abstract base mapper that capitalizes the names, and two
concrete mappers re-using the functionality of the abstract base mapper:

```kotlin 
abstract class UppercaseEnumMapper<FROM : Enum<FROM>, TO : Enum<TO>>(
    private val entries: EnumEntries<TO>
) : EnumMappie<FROM, TO>() {
    override fun map(from: FROM) = 
        entries.first { it.name == from.name.uppercase() }
}

object GenderMapper : UppercaseEnumMapper<Gender, GenderDto>(GenderDto.entries)
object EyeColorMapper : UppercaseEnumMapper<EyeColor, EyeColorDto>(EyeColorDto.entries)
```

Another possible use-case is when you want to combine the mapping logic of the abstract base class and
the child class. This will happen when the abstract base class contains a `mapping { ... }` block.