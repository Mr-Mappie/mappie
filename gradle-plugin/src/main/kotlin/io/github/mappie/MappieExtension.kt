package io.github.mappie

import org.gradle.api.Project
import org.gradle.api.provider.Property

abstract class MappieExtension(private val project: Project) {

    private val extensions = mutableMapOf<String, Any>()

    internal val strictness: MappieStrictnessExtension get() =
        extensions.getOrPut(MappieStrictnessExtension.NAME) {
            project.objects.newInstance(MappieStrictnessExtension::class.java)
        } as MappieStrictnessExtension

    /**
     * Configuration options for the strictness of validations.
     */
    fun strictness(configuration: MappieStrictnessExtension.() -> Unit) {
        configuration(strictness)
    }
}

abstract class MappieStrictnessExtension {

    /**
     * Whether to require all enum sources have a defined target.
     */
    abstract val enums: Property<Boolean>

    /**
     * Whether to require called elements to be visible from the current scope.
     */
    abstract val visibility: Property<Boolean>

    internal companion object {
        const val NAME = "mappie-strictness-extension"
    }
}
