---
title: "Enum Mapping Configuration"
summary: "Enum mapping configuration."
eleventyNavigation:
  key: Enum Mapping Configuration
  parent: Enum Mapping
  order: 12
---

By default, all enum sources must have a defined target. This can be disabled by adding the following
configuration to the `build.gradle.kts` file

```kotlin
mappie {
    strictness {
        enums = false // Disable validating that all enum sources have a corresponding target 
    }
}
```

Note that this might result in a [NoWhenBranchMatchedException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-no-when-branch-matched-exception/) exception being thrown at runtime.