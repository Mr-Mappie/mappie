---
title: "Configuration"
summary: "Configure Mappie."
eleventyNavigation:
  key: Configuration
  parent: Getting Started
  order: 3
---

Mappie can be configured via Gradle or per Mapper. The following configuration options are available
```kotlin
mappie {
    warningsAsErrors = true // Enable reporting warnings as errors
    useDefaultArguments = false // Disable using default arguments in implicit mappings.
    strictness {
        visibility = true // Allow calling constructors not visible from the calling scope
        enums = false // Do not report an error if not all enum sources are mapped 
    }
}
```

Local configuration options are applied as annotations on the class level of mappers, and will override the global 
configuration option on a per-mapper basis. 

The following options exist with their corrosponding default values
| Gradle Option           | Annotation             | Default Value |
|-------------------------|------------------------|---------------|
| `warningsAsErrors`      |                        | `false`       |
| `useDefaultArguments`   | `@UseDefaultArguments` | `true`        |
| `strictness.visibility` | `@UseStrictVisibility` | `false`       |
| `strictness.enums`      | `@UseStrictEnums`      | `true`        |
