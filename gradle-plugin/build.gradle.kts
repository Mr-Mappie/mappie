import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.convention.mappie.jvm)
    `java-test-fixtures`
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.com.github.gmazzo.buildconfig)
}

kotlin {
    coreLibrariesVersion = "2.0.0"
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        apiVersion = KotlinVersion.KOTLIN_2_0
    }
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin)

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
}

gradlePlugin {
    website = "https://mappie.tech/"
    vcsUrl = "https://github.com/Mr-Mappie/mappie.git"
    plugins {
        create("mappie") {
            id = "tech.mappie.plugin"
            displayName = "Mappie Gradle Plugin"
            description = "Kotlin compiler plugin for generating object mappers"
            tags = setOf("kotlin", "object-mapping", "compiler-plugin")
            implementationClass = "tech.mappie.MappieGradlePlugin"
        }
    }
}

buildConfig {
    packageName = group.toString()
    buildConfigField("GROUP_ID", group.toString())
    buildConfigField("PLUGIN_ID", "mappie-compiler-plugin")
    buildConfigField("COMPILER_PLUGIN_ID", "mappie")
    buildConfigField("VERSION", version.toString())
}

tasks.test {
    useJUnitPlatform()

    dependsOn("publishToMavenLocal")
    dependsOn(":compiler-plugin:publishToMavenLocal")
    dependsOn(":mappie-api:publishToMavenLocal")
    dependsOn(":modules:kotlinx-datetime:publishToMavenLocal")

    testLogging {
        showCauses = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}
