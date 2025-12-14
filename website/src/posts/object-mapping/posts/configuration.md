---
title: "Object Mapping Configuration"
summary: "Object Mapping Configuration."
eleventyNavigation:
  key: Object Mapping Configuration
  parent: Object Mapping
  order: 14
---


## Java Nullability Errors
When mapping from Java sources, assigning a platform type to a non-nullable target produces a warning by default.
This can be disabled locally by adding `@UseStrictPlatformTypeNullabilityValidation(false)` to the mapper, or globally by adding the
following configuration to the Gradle build file
```kotlin
mappie {
    strictness {
        platformTypeNullability = false // Allow unsafe assigning Java platform types to non-nullable targets
    }
}
```

## Ignoring Visibility
By default, only constructors visible from the current scope can be used to called in a mapping.
This can be disabled locally by adding `@UseStrictVisibility(true)` to the mapper, or globally
by adding the following configuration to the Gradle build file
```kotlin
mappie {
    strictness {
        visibility = true // Allow calling constructors not visible from the calling scope
    }
}
```

## Using Default Arguments
By default, default arguments are used in implicit mappings. If this is unwanted, this can be disabled 
locally adding `@UseDefaultArguments(false)` to the mapper, or globally by adding
the following configuration tot the Gradle build file
```kotlin
mappie {
    useDefaultArguments = false // Disable using default arguments in implicit mappings
}
```