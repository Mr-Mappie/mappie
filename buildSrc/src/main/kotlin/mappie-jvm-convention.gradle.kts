plugins {
    id("mappie-convention")
    kotlin("jvm")
}

tasks.withType<Test> {
    useJUnitPlatform()

    maxHeapSize = "1024m"
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}