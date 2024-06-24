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
    warningsAsErrors = true // Enable reporting warnings as erros
    strictness {
        visibility = false // Disable only selecting visible constructors
        enums = false // Disable validating that all enum sources have a corresponding target
    }
}
```