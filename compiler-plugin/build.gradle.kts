import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(libs.kotlin.compiler.embeddable)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.compiler.embeddable)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "compiler-plugin"
            from(components["kotlin"])
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add(
        "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI"
    )
}