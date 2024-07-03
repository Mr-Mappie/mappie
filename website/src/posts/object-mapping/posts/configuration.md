---
title: "Object Mapping Configuration"
summary: "Object Mapping Configuration."
eleventyNavigation:
  key: Object Mapping Configuration
  parent: Object Mapping
  order: 10
---

By default, only constructors visible from the current scope can be used to called in a mapping.
This can be disabled by adding the following configuration to the `build.gradle.kts` file

```kotlin
mappie {
    strictness {
        visibility = true // Allow calling constructors not visible from the calling scope
    }
}
```