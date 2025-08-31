import org.jreleaser.model.Signing

plugins {
    id("mappie-convention")
    java
    alias(libs.plugins.jreleaser)
}

tasks.jreleaserFullRelease.configure {
    notCompatibleWithConfigurationCache("Disable configuration-cache for jreleaser")
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
                buildList {
                    if (System.getenv("RELEASE_API") == "true") {
                        add("mappie-api")
                    }
                    if (System.getenv("RELEASE_KOTLINX_DATETIME") == "true") {
                        add("mappie-kotlinx-datetime")
                    }
                    if (System.getenv("RELEASE_MAVEN_PLUGIN") == "true") {
                        add("maven-plugin")
                    }
                    if (System.getenv("RELEASE_COMPILER_PLUGIN") == "true") {
                        add("compiler-plugin")
                    }
                }.forEach {
                    create(it) {
                        active = org.jreleaser.model.Active.ALWAYS
                        url = "https://central.sonatype.com/api/v1/publisher"
                        stagingRepository(project(":$it").layout.buildDirectory.dir("staging-deploy").get().toString())
                        username = properties["mavenCentralUsername"] as? String
                        password = properties["mavenCentralPassword"] as? String
                        applyMavenCentralRules = true
                        verifyPom = false
                        retryDelay = 20
                    }
                }
            }
        }
    }
}