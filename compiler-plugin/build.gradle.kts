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

    compileOnly(project(":mappie-api"))
    compileOnly(project(":modules:kotlinx-datetime"))

    testImplementation(project(":mappie-api"))
    testImplementation(project(":testutil"))
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.kotlin.compiler.embeddable)
}

tasks.jar {
    from(project(":mappie-api").sourceSets.named("jvmMain").map { it.output })
    from(project(":modules:kotlinx-datetime").sourceSets.named("jvmMain").map { it.output })
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
            from(components["java"])
            artifactId = "mappie-compiler-plugin"
            mappiePom(name = "tech.mappie:mappie-compiler-plugin")
        }
    }

    if (System.getenv("RELEASE_COMPILER_PLUGIN").toBoolean()) {
        repositories {
            maven {
                url = uri(rootProject.layout.buildDirectory.file("staging-deploy"))
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
}
