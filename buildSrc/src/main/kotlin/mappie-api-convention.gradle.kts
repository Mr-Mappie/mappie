plugins {
    id("mappie-convention")
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
}

val dokkaHtml by tasks.dokkaGeneratePublicationHtml
tasks.register<Jar>("javadocJar") {
    group = "build"
    description = "Assemble a javadoc jar containing the Dokka pages of the 'main' feature."
    archiveClassifier = "javadoc"
    from(dokkaHtml.outputDirectory)
    dependsOn(dokkaHtml)
}

tasks.register<Jar>("emptyJar") {
    group = "build"
    description = "Assemble an empty jar."
}

tasks.withType<Test> {
    useJUnitPlatform()

    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}