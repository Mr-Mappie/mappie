plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

kotlin {
    explicitApi()

    withSourcesJar()

    jvm()
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}
