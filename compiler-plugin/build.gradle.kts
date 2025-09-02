import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("mappie-jvm-convention")
    alias(libs.plugins.com.github.gmazzo.buildconfig)
    id("maven-publish")
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(project(":mappie-api"))

    testImplementation(project(":mappie-api"))
    testImplementation(project(":testutil"))
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlin.compiler.embeddable)
}

java {
    withSourcesJar()
    withJavadocJar()
}

buildConfig {
    buildConfigField("VERSION", version.toString())
}

publishing {
    publications {
        create<MavenPublication>("kotlin") {
            artifactId = "mappie-compiler-plugin"
            mappiePom(name = "tech.mappie:compiler-plugin")
        }
    }

    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
}
