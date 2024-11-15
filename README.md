[![Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/tech.mappie.plugin)](https://plugins.gradle.org/plugin/tech.mappie.plugin)
[![Maven Central](https://img.shields.io/maven-metadata/v.svg?label=maven-central&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Ftech%2Fmappie%2Fmappie-api%2Fmaven-metadata.xml)](https://central.sonatype.com/artifact/tech.mappie/mappie-api)
[![Continuous Integration](https://github.com/mr-mappie/mappie/workflows/Build/badge.svg)](https://github.com/mr-mappie/mappie/actions)
[![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-green.svg)](https://shields.io/)

# Mappie
Mappie is a Kotlin compiler plugin which generates code to simplify developing object mapping code. Writing object 
mapping code is often a mind-numbing and error-prone task. Reducing this development effort will lead to a more 
pleasant programming experience, and less risk of bugs.

Mappie offers the following advantages:
* All code is **generated at compile-time**, without the need for any reflection at runtime. Making the generated code
have the same runtime performance as if one would write the object mapping code manually.
* You write **Kotlin code instead of String references**, allowing your IDE to support you with automatic refactorings and 
auto-complete.
* Fully designed with Kotlin in mind, making the code **idiomatic and completely type-safe**.
* Mappie is easy to use and **infers as much as possible**.
* **Flawless compatibility** with other platforms, such as Java types.
* **Error messages are informative** and suggest concrete actions.

Visit the [project documentation](https://mappie.tech) for more in-depth information.

## Example
The following snippet provides a minimal peek into the idiomatic style of Mappie. Suppose we have the data class `Person`
which we want to map to the data class `PersonDto`
```kotlin
data class Person(
    val name: String, 
    val surname: String,
    val age: Int,
)

data class PersonDto(
    val name: String, 
    val age: Int, 
)
```
We can achieve generating such a mapper using the following object mapper.
```kotlin
object PersonToPersonDtoMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person) = mapping {
        to::name fromValue "${from.name} ${to.name}"
    }
}
```

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