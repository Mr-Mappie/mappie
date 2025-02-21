import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.com.github.gmazzo.buildconfig)
    id("maven-publish")
}

dependencies {
    compileOnly(libs.maven.project)
    compileOnly(libs.maven.core)
    compileOnly(libs.kotlin.maven.plugin)

    runtimeOnly(project(":compiler-plugin"))

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.maven.invoker)
}

buildConfig {
    packageName = group.toString()
    buildConfigField("COMPILER_PLUGIN_ID", "mappie")
    buildConfigField("VERSION", version.toString())
    sourceSets.named("test") {
        buildConfigField("MAVEN_WRAPPER_PATH", layout.projectDirectory.file("src/test/resources/maven").asFile.absolutePath)
    }
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
    publications {
        create<MavenPublication>("java-maven-plugin") {
            artifactId = "mappie-maven-plugin"
            from(components["java"])
            mappiePom(name = "tech.mappie:maven-plugin")
        }
    }
}

tasks.test {
    useJUnitPlatform()

    dependsOn("publishToMavenLocal")
    dependsOn(":mappie-api:publishToMavenLocal")

    testLogging {
        showCauses = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}