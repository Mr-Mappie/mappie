import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":compiler-plugin"))

    implementation(project(":api"))

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.params)
}

tasks.test {
    useJUnitPlatform()
}
