---
title: "Enum Mapping"
summary: "Performing enum mapping."
eleventyNavigation:
  key: Enum Mapping
  parent: Usage
  order: 2
---

# Summary
Mappie supports creating enum mappers via the base class `EnumMapper`.

Suppose we have an enum class `Color`
```kotlin
enum class Color { RED, GREEN, BLUE; }
```
and an enum class `Colour`
```kotlin
enum class Colour { RED, GREEN, GLUE; }
```
The entries of both are identical, so we do not have to declare any mapping explicitly
```kotlin
object ColorMapper : EnumMapper<Color, Colour>() {
    override fun map(from: Color): Colour = mapping()
}
```

Now suppose `Color`{:.kotlin} has an extra entry: `Color.ORANGE`, whilst `Colour` does
not have `Colour.ORANGE`, but does have `Colour.OTHER`. We can map `Colour.ORANGE` to `Colour.OTHER` via

```kotlin
object ColorMapper : EnumMapper<Color, Colour>() {
    override fun map(from: Color): Colour = mapping {
        Colour.OTHER mappedFromEnumEntry Color.ORANGE
    }
}
```

# Configuration options

