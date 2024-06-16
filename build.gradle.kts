plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.sonarqube)
}

allprojects {
    group = "io.github.mappie"
    version = "0.1.0"
}

sonar {
    properties {
        property("sonar.projectKey", "Mr-Mappie_mappe")
        property("sonar.organization", "mappie")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}