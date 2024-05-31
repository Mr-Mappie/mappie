plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-gradle-plugin")
    id("maven-publish")
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)
}

gradlePlugin {
    plugins {
        create("analysis") {
            id = "io.github.stefankoppier.kotlin.mapping"
            implementationClass = "io.github.stefankoppier.mapping.MappingSubPlugin"
        }
    }
}
