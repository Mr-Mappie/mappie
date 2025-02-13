---
title: "Entry Mapping"
summary: "Mapping enum entry values."
eleventyNavigation:
  key: Entry Mapping
  parent: Enum Mapping
  order: 15
---

Not all enums classes that one wants to map have identical entries, or are not even both an enum class. If this is the case,
Mappie cannot determine which source must map to which target.

Suppose `Color` has an extra entry `Color.ORANGE`, whilst `Colour` does
not have `Colour.ORANGE`, but does have `Colour.OTHER`. In other words 
```kotlin
enum class Color { RED, GREEN, BLUE, ORANGE; }

enum class Colour { RED, GREEN, BLUE, OTHER; }
```

We can generate a complete mapper by mapping `Colour.ORANGE` to `Colour.OTHER` via `fromEnumEntry`
```kotlin
object ColorMapper : EnumMappie<Color, Colour>() {
    override fun map(from: Color): Colour = mapping {
        Colour.OTHER fromEnumEntry Color.ORANGE
    }
}
```

## Throwing an Exception
We can also make the generated mapper throw an exception, similar to setting the global enum strictness property
to true, but in a fine-grained manner. This can be done by mapping via `thrownByEnumEntry`. 

For example the following snippet will generate a mapper in which the `map` function will throw an `IllegalStateException`
when called with argument `Color.ORANGE`.
```kotlin
object ColorMapper : EnumMappie<Color, Colour>() {
    override fun map(from: Color): Colour = mapping {
        IllegalStateException() thrownByEnumEntry Color.ORANGE
    }
}
```
