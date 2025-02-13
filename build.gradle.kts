import org.jreleaser.model.Signing

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.jreleaser)
    id("jacoco-report-aggregation")
}

allprojects {
    group = "tech.mappie"
    description = "Kotlin compiler plugin for generating object mappers"
    version = properties["version"] as String
}

dependencies {
    jacocoAggregation(project(":compiler-plugin"))
    jacocoAggregation(project(":gradle-plugin"))
}

sonar {
    properties {
        property("sonar.projectKey", "Mr-Mappie_mappie")
        property("sonar.organization", "mappie")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.qualitygate.wait", "true")
        property("sonar.branch.name", "main")
        property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory
                .file("reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml")
                .get().asFile.absolutePath
        )
    }
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