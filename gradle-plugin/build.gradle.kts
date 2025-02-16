plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
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

tasks.register("updateMappieProperties") {
    group = "build"
    description = "Update mappie.properties file for Gradle plugin."

    val projectVersion = project.version.toString()
    val propertiesFile = layout.buildDirectory.file("resources/main/mappie.properties")
    outputs.file(propertiesFile)

    tasks.findByName("sourcesJar")?.dependsOn(this)

    doLast {
        propertiesFile.get().asFile.writeText("VERSION=$projectVersion")
    }
}

tasks.named("processResources") { dependsOn("updateMappieProperties") }

tasks.test {
    useJUnitPlatform()

    dependsOn("publishToMavenLocal")
    dependsOn(":compiler-plugin:publishToMavenLocal")
    dependsOn(":mappie-api:publishToMavenLocal")

    finalizedBy(tasks.jacocoTestReport)

    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}