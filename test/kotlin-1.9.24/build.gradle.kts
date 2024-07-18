plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("tech.mappie.plugin") version "+"
}

dependencies {
    implementation("tech.mappie:mappie-api:+")

    testImplementation(kotlin("test"))
}

tasks.register<Sync>("sources") {
    from(project.rootDir.resolve("../src"))
    into(project.rootDir.resolve("src"))
}

tasks.compileKotlin {
    dependsOn("sources")
}

tasks.test {
    useJUnitPlatform()
}