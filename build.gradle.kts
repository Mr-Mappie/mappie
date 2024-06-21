plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.sonarqube)
}

allprojects {
    group = "tech.mappie"
    version = "0.1.0"
}

sonar {
    properties {
        property("sonar.projectKey", "Mr-Mappie_mappie")
        property("sonar.organization", "mappie")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.qualitygate.wait", "true")
    }
}