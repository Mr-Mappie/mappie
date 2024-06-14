package io.github.mappie

import org.gradle.api.Project
import org.gradle.api.provider.Property

abstract class MappieExtension(private val project: Project) {

    private val extensions = mutableMapOf<String, Any>()

    internal val strictness: MappieStrictnessExtension get() =
        extensions.getOrPut(MappieStrictnessExtension.NAME) {
            project.objects.newInstance(MappieStrictnessExtension::class.java)
        } as MappieStrictnessExtension

    fun strictness(configuration: MappieStrictnessExtension.() -> Unit) {
        configuration(strictness)
    }
}

abstract class MappieStrictnessExtension {

    abstract val enums: Property<Boolean>

    internal companion object {
        const val NAME = "mappie-strictness-extension"
    }
}
