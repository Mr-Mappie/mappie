plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    id("maven-publish")
}

kotlin {
    explicitApi()
    withSourcesJar(publish = true)
    applyDefaultHierarchyTemplate()

    jvm {
        withSourcesJar(publish = true)
    }

    androidNativeX64()
    androidNativeArm64()

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    tvosX64()
    tvosSimulatorArm64()
    tvosArm64()

    js {
        browser()
        nodejs()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    mingwX64()

    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()
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

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }

    publications.configureEach {
        if (this is MavenPublication) {
            artifact(tasks["javadocJar"])
            // jreleaser workaround
            if (name != "jvm" && name != "kotlinMultiplatform") {
                artifact(tasks["emptyJar"])
            }
            mappiePom(name = "tech.mappie:compiler-plugin")
        }
    }
}