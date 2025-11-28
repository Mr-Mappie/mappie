package tech.mappie.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

class MappieConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        group = "tech.mappie"
        description = "Kotlin compiler plugin for generating object mappers"
        val versionProperty = findProperty("version")?.toString()
            ?: error("Missing 'version' property. Define it in gradle.properties.")
        version = versionProperty
    }
}
