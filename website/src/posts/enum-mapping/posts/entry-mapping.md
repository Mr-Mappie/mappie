---
title: "Entry Mapping"
summary: "Mapping enum entry values."
eleventyNavigation:
  key: Entry Mapping
  parent: Enum Mapping
  order: 9
---

Not all enums classes that one wants to map have identical entries. If this is the case,
Mappie cannot determine which source must map to which target.

Suppose `Color` has an extra entry `Color.ORANGE`, whilst `Colour` does
not have `Colour.ORANGE`, but does have `Colour.OTHER`. In other words 
```kotlin
enum class Color { RED, GREEN, BLUE, ORANGE; }

enum class Colour { RED, GREEN, BLUE, OTHER; }
```

We can generate a complete mapper by mapping `Colour.ORANGE` to `Colour.OTHER` via `mappedFromEnumEntry`
```kotlin
object ColorMapper : EnumMappie<Color, Colour>() {
    override fun map(from: Color): Colour = mapping {
        Colour.OTHER mappedFromEnumEntry Color.ORANGE
    }
}
```