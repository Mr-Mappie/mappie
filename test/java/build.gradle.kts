plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("tech.mappie.plugin") version "+"
}

dependencies {
    implementation("tech.mappie:mappie-api:+")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}