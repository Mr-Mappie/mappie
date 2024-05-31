import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":processor"))

    implementation(project(":api"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
