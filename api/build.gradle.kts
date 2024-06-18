plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "mappie-api"
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()
}