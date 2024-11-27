plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
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

tasks.register("updateCompilerPluginVersion") {
    group = "build"
    description = "Update version.properties file for Gradle plugin."
    doLast {
        val directory = project.mkdir("src/main/resources")
        File(directory, "version.properties").writeText("version=${project.version}")
    }
}

tasks.compileKotlin {
    dependsOn("updateCompilerPluginVersion")
}

tasks.test {
    useJUnitPlatform()

    dependsOn(project.tasks.publishToMavenLocal)
    dependsOn(project(":compiler-plugin").tasks.publishToMavenLocal)
    dependsOn(project(":mappie-api").tasks.named("publishKotlinMultiplatformPublicationToMavenLocal"))
    dependsOn(project(":mappie-api").tasks.named("publishJvmPublicationToMavenLocal"))

    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}