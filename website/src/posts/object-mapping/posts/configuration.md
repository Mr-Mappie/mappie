---
title: "Object Mapping Configuration"
summary: "Object Mapping Configuration."
eleventyNavigation:
  key: Object Mapping Configuration
  parent: Object Mapping
  order: 9
---

By default, only constructors visible from the current scope can be used to called in a mapping.
This can be disabled by adding the following configuration to the `build.gradle.kts` file

```kotlin
mappie {
    strictness {
        visibility = false // Disable only selecting visible constructors
    }
}
```