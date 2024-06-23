import org.jreleaser.model.Signing

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.jreleaser)
}

allprojects {
    group = "tech.mappie"
    version = "0.1.0"
    description = "Kotlin compiler plugin for generating object mappers"
}

sonar {
    properties {
        property("sonar.projectKey", "Mr-Mappie_mappie")
        property("sonar.organization", "mappie")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.qualitygate.wait", "true")
    }
}

jreleaser {
    project {
        authors.add("Stefan Koppier")
        license = "Apache-2.0"
        links {
            homepage = "https://mappie.tech"
        }
        inceptionYear = "2024"
    }
}

jreleaser {
    signing {
        active = org.jreleaser.model.Active.ALWAYS
        armored = true
        mode = Signing.Mode.COMMAND
        passphrase = properties["signing.passphrase"] as? String
    }
    release {
        github {
            token = properties["release.github.token"] as? String
            draft = true
        }
    }
    deploy {
        maven {
            mavenCentral {
                active = org.jreleaser.model.Active.ALWAYS
                create("mappie-api") {
                    active = org.jreleaser.model.Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(project(":mappie-api").layout.buildDirectory.dir("staging-deploy").get().toString())
                    username = properties["mavenCentralUsername"] as? String
                    password = properties["mavenCentralPassword"] as? String
                    applyMavenCentralRules = true
                    deploymentId = "d5bfe6b5-6fe5-4342-868b-c47ae4635249"
                }
                create("compiler-plugin") {
                    active = org.jreleaser.model.Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(project(":compiler-plugin").layout.buildDirectory.dir("staging-deploy").get().toString())
                    username = properties["mavenCentralUsername"] as? String
                    password = properties["mavenCentralPassword"] as? String
                    applyMavenCentralRules = true
                    deploymentId = "a6d8ebaf-ee2d-444e-b808-4e965b9491ae"
                }
            }
        }
    }
}
