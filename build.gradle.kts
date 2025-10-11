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
        verify = false
    }
    release {
        github {
            draft = true
        }
    }
    deploy {
        maven {
            mavenCentral {
                active = org.jreleaser.model.Active.ALWAYS

                create("mappie") {
                    active = org.jreleaser.model.Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    username = properties["mavenCentralUsername"] as? String
                    password = properties["mavenCentralPassword"] as? String
                    applyMavenCentralRules = true
                    verifyPom = false
                    retryDelay = 20

                    stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
                }
            }
        }
    }
}