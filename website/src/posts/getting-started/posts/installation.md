---
title: "Installation"
summary: "Applying Mappie to your project."
eleventyNavigation:
  key: Installation
  parent: Getting Started
  order: 2
---

Mappie is a Kotlin compiler plugin. The preferred way to use Mappie is to apply the Gradle plugin which in term 
applies the compiler plugin to the Kotlin compilation.

This can be achieved by adding the following snippet to the `build.gradle.kts` file.
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