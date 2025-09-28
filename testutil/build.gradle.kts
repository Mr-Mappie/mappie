import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("mappie-jvm-convention")
}

dependencies {
    implementation(project(":mappie-api"))
    implementation(project(":compiler-plugin"))
    implementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.classgraph)
    implementation(libs.okio)
    implementation(libs.assertj.core)
    implementation(libs.junit.jupiter.api)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}
