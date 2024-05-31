plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.kotlin.compiler.embeddable)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "mapping-processor"
            from(components["kotlin"])
        }
    }
}