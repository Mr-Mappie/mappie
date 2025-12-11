import tech.mappie.buildlogic.mappiePom

plugins {
    alias(libs.plugins.convention.mappie.api)
    `maven-publish`
}

kotlin {
    explicitApi()
    withSourcesJar(publish = true)
    applyDefaultHierarchyTemplate()

    jvm {
        withSourcesJar(publish = true)
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.mappieApi)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}

publishing {
    publications.withType(MavenPublication::class.java).configureEach {
        artifactId = artifactId.replace("kotlinx-collections-immutable", "module-kotlinx-collections-immutable")
        artifact(tasks.named("javadocJar"))
        // jreleaser workaround
        if (name != "jvm" && name != "kotlinMultiplatform") {
            artifact(tasks.named("emptyJar"))
        }
        mappiePom(name = "tech.mappie:module-kotlinx-collections-immutable")
    }

    if (System.getenv("RELEASE_MODULE_KOTLINX_COLLECTIONS_IMMUTABLE").toBoolean()) {
        repositories {
            maven {
                url = uri(rootProject.layout.buildDirectory.file("staging-deploy"))
            }
        }
    }
}
