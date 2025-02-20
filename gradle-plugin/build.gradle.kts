plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.com.github.gmazzo.buildconfig)
    id("jacoco")
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)

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

    finalizedBy(tasks.jacocoTestReport)

    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}