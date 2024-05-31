plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "mapping-api"
            from(components["kotlin"])
        }
    }
}