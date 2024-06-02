plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-gradle-plugin")
    id("maven-publish")
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin.api)
}

gradlePlugin {
    plugins {
        create("mappie") {
            id = "io.github.mappie"
            displayName = "Mappie Gradle Plugin"
            description = "Kotlin compiler plugin for generating mapping functions"
            implementationClass = "io.github.mappie.MappieGradlePlugin"
        }
    }
}
