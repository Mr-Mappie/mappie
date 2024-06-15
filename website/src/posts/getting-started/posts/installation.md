---
title: "Installation"
summary: ""
eleventyNavigation:
  key: Installation
  parent: Getting Started
  order: 2
---

Mappie is a Kotlin compiler plugin. The most convenient way to use compiler plugins is
to apply the Gradle plugin which in term applies the compiler plugin to the Kotlin compilation.

This can be achieved by adding the following snippet to the `build.gradle.kts` file.
```kotlin
plugins {
    id("io.github.mappie") version "x.y.z"
}
```
The `mappie-api` dependency must be added to the `build.gradle.kts` file for the programming interface
```kotlin
dependencies {
    implementation("io.github.mappie:mappie-api:x.y.z")
}
```
The most recent version of Mappie can be found at the [releases](https://github.com/Mr-Mappie/mappie/releases) page.