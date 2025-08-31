plugins {
    id("mappie-api-convention")
    id("maven-publish")
}

dependencies {
    jvmMainImplementation(project(":mappie-api"))
    jvmMainImplementation(libs.kotlinx.datetime)

    jvmTestImplementation(kotlin("test"))
    jvmTestImplementation(testFixtures(project(":compiler-plugin")))
    jvmTestImplementation(libs.assertj.core)
}