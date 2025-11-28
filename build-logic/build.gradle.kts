plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation(libs.kotlin.multiplatform.gradle.plugin)
    implementation(libs.kotlin.jvm.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("mappieConvention") {
            id = "convention-mappie"
            implementationClass = "tech.mappie.buildlogic.MappieConventionPlugin"
        }
        register("mappieJvmConvention") {
            id = "convention-mappie-jvm"
            implementationClass = "tech.mappie.buildlogic.MappieJvmConventionPlugin"
        }
        register("mappieApiConvention") {
            id = "convention-mappie-api"
            implementationClass = "tech.mappie.buildlogic.MappieApiConventionPlugin"
        }
    }
}
