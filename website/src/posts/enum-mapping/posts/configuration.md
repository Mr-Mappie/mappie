---
title: "Enum Mapping Configuration"
summary: "Enum mapping configuration."
eleventyNavigation:
  key: Enum Mapping Configuration
  parent: Enum Mapping
  order: 17
---

By default, all enum sources must have a defined target. This can be disabled locally by adding `@UseStrictEnums(false)` 
to the mapper, or globally adding the following configuration to the Gradle build file
```kotlin
mappie {
    strictness {
        enums = false // Do not report an error if not all enum sources are mapped 
    }
}
```

Note that this might result in a [NoWhenBranchMatchedException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-no-when-branch-matched-exception/)
being thrown at runtime.