plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("tech.mappie.plugin") version "+"
}

dependencies {
    implementation("tech.mappie:mappie-api:+")
}

tasks.register<Copy>("sources") {
    from(project.rootDir.resolve("../src"))
    into(project.rootDir.resolve("src"))
}