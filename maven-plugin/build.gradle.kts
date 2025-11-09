import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import tech.mappie.buildlogic.mappiePom

plugins {
    alias(libs.plugins.convention.mappie.jvm)
    alias(libs.plugins.com.github.gmazzo.buildconfig)
    `maven-publish`
}

dependencies {
    compileOnly(libs.maven.project)
    compileOnly(libs.maven.core)
    compileOnly(libs.kotlin.maven.plugin)

    runtimeOnly(projects.compilerPlugin)

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

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("java-maven-plugin") {
            artifactId = "mappie-maven-plugin"
            from(components["java"])
            mappiePom(name = "tech.mappie:maven-plugin")
        }
    }

    if (System.getenv("RELEASE_MAVEN_PLUGIN").toBoolean()) {
        repositories {
            maven {
                url = uri(rootProject.layout.buildDirectory.file("staging-deploy"))
            }
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
