---
title: "Installation"
summary: "Applying Mappie to your project."
eleventyNavigation:
  key: Installation
  parent: Getting Started
  order: 2
---

Mappie is a Kotlin compiler plugin which comes with a Gradle plugin to automatically apply and configure the compiler 
plugin. We can apply Mappie by adding the following plugin to the `build.gradle.kts` file
```kotlin
plugins {
    id("tech.mappie.plugin") version "x.y.z"
}
```
The `mappie-api` dependency must be added to the `build.gradle.kts` file for the programming interface
```kotlin
dependencies {
    implementation("tech.mappie:mappie-api:x.y.z")
}
```
The most recent version of Mappie can be found at the [releases](https://github.com/Mr-Mappie/mappie/releases) page.

## Compatibility

Mappie is dependent on compiler internals, which might be unstable. These dependencies are kept to a minimum,
but are unavoidable. Mappie is tested and compatible with Kotlin versions `1.9.24`, `2.0.0`, and higher.

Versions below `1.9.24` might work, but are untested. If you encounter an issue, please report this as a bug. 
Note that incompatible versions will possibly lead to compilation failure, and never to runtime risks.
