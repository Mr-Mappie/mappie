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
    dependsOn(":compiler-plugin:publishToMavenLocal")
    dependsOn(":mappie-api:publishToMavenLocal")
}