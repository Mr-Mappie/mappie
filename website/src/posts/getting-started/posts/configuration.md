---
title: "Configuration"
summary: "Configure Mappie."
eleventyNavigation:
  key: Configuration
  parent: Getting Started
  order: 3
---

Mappie can be configured via Gradle. The following global configuration options are available
```kotlin
mappie {
    warningsAsErrors = true // Enable reporting warnings as errors
    useDefaultArguments = false // Disable using default arguments as sources.
    strictness {
        visibility = true // Allow calling constructors not visible from the calling scope
        enums = true // Do not report an error if not all enum sources are mapped 
    }
}
```
with the following default values

| Option                   | Default Value |
|--------------------------|---------------|
| `warningsAsErrors`       | `false`       |
| `useDefaultArguments`    | `true`        |
| `strictness.visibility`  | `false`       |
| `strictness.enums`       | `false`       |
