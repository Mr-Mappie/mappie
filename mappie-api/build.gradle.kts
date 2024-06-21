plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

kotlin {
    explicitApi()

    withSourcesJar()

    jvm()
    js()
}
