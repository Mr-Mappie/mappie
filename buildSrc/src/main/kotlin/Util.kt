import org.gradle.api.publish.maven.MavenPublication

fun MavenPublication.mappiePom(name: String) =
    pom {
        this.name.set(name)
        description.set("Kotlin Compiler Plugin for generating object mappers")
        url.set("https://github.com/Mr-Mappie/mappie")

        developers {
            developer {
                id.set("stefankoppier")
                this.name.set("Stefan Koppier")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/Mr-Mappie/mappie.git")
            developerConnection.set("scm:git:git://github.com/Mr-Mappie/mappie.git")
            url.set("https://github.com/Mr-Mappie/mappie/tree/main")
        }

        licenses {
            license {
                this.name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/Mr-Mappie/mappie/issues")
        }
    }
