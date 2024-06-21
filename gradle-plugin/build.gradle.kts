plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin.api)
}

gradlePlugin {
    website = "https://mappie.tech/"
    plugins {
        create("mappie") {
            id = "org.mappie.plugin"
            displayName = "Mappie Gradle Plugin"
            description = "Kotlin compiler plugin for generating object mappers"
            implementationClass = "org.mappie.MappieGradlePlugin"
            tags = setOf("kotlin", "object-mapping", "compiler-plugin")
        }
    }
}