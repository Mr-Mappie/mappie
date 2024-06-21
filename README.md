[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Mr-Mappie_mappie&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Mr-Mappie_mappie)

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
    id("org.mappie.plugin") version "x.y.z"
}
```
The `mappie-api` dependency must be added to the `build.gradle.kts` file for the programming interface
```kotlin
dependencies {
    implementation("org.mappie:mappie-api:x.y.z")
}
```