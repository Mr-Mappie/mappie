import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
    id("signing")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(project(":mappie-api"))

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.compiler.embeddable)
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = properties["ossrhUsername"] as String
                password = properties["ossrhPassword"] as String
            }
        }
    }
    publications {
        create<MavenPublication>("kotlin") {
            artifactId = "mappie-compiler-plugin"
            from(components["kotlin"])
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["kotlin"])
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add(
        "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI"
    )
}