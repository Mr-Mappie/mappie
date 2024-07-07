plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("tech.mappie.plugin") version "0.3.0"
}

dependencies {
    implementation("tech.mappie:mappie-api:0.3.0")
}

tasks.register<Copy>("sources") {
    from(project.rootDir.resolve("../src"))
    into(project.rootDir.resolve("src"))
}