import kotlin.io.path.createDirectories

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin.api)
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

tasks.register("useVersion") {
    val file = layout.projectDirectory.file("src/main/resources/version.properties").asFile
    file.toPath().createDirectories()
    file.createNewFile()
    file.writeText("version=${project.version}")
}

tasks.compileKotlin {
    dependsOn("useVersion")
}