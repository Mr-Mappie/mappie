plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.multiplatform.gradle.plugin)
    implementation(libs.kotlin.jvm.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
}