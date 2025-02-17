plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

// TODO: move dependencies to libs.versions.toml
dependencies {
    compileOnly("org.apache.maven:maven-project:2.2.1")
    compileOnly("org.apache.maven:maven-core:3.9.8")
    compileOnly("org.jetbrains.kotlin:kotlin-maven-plugin:2.1.10")

    runtimeOnly(project(":compiler-plugin"))

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation("org.apache.maven.shared:maven-invoker:3.3.0")
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
    publications {
        create<MavenPublication>("java-maven-plugin") {
            artifactId = "mappie-maven-plugin"
            from(components["java"])
            mappiePom(name = "tech.mappie:compiler-plugin")
        }
    }
}

// TODO: duplicate of the one in :gradle-plugin. Move to buildSrc.
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

    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}