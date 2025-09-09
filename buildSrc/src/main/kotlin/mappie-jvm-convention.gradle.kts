plugins {
    id("mappie-convention")
    kotlin("jvm")
}

tasks.withType<Test> {
    useJUnitPlatform()

    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}