import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("mappie-jvm-convention")
    id("java-test-fixtures")
    id("maven-publish")
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(project(":mappie-api"))

    testFixturesImplementation(project(":mappie-api"))
    testFixturesImplementation(kotlin("test"))
    testFixturesImplementation(kotlin("reflect"))
    testFixturesImplementation(libs.kotlin.compiler.embeddable)
    testFixturesImplementation(libs.classgraph)
    testFixturesImplementation(libs.okio)
    testFixturesImplementation(libs.assertj.core)
    testFixturesImplementation(libs.junit.jupiter.api)

    testImplementation(project(":mappie-api"))
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlin.compiler.embeddable)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("kotlin") {
            artifactId = "mappie-compiler-plugin"

            from((components["java"] as AdhocComponentWithVariants).apply {
                withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
                withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
            })

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

tasks.compileTestFixturesKotlin.configure {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}
