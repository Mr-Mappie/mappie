[![Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/tech.mappie.plugin)](https://plugins.gradle.org/plugin/tech.mappie.plugin)
[![Maven Central](https://img.shields.io/maven-metadata/v.svg?label=maven-central&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Ftech%2Fmappie%2Fmappie-api%2Fmaven-metadata.xml)](https://central.sonatype.com/artifact/tech.mappie/mappie-api)
[![Continuous Integration](https://github.com/mr-mappie/mappie/workflows/Build/badge.svg)](https://github.com/mr-mappie/mappie/actions)
[![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-green.svg)](https://shields.io/)

# Mappie
Mappie is a Kotlin compiler plugin which generates code simplifying developing object mapping code. Mappie generates all
code at compile-time, allowing for fast mapping without the need for any reflection at runtime.

Mappie can generate mappers for objects and enums. It aims to be easy to use, infer as much implicit mappings as possible,
have a consistent API, and have the same runtime performance as if one would write the mapping code manually.

Visit the [project documentation](https://mappie.tech) for more in-depth information.

## Usage
Mappie can be used by adding the following snippet to your `build.gradle.kts` file.
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