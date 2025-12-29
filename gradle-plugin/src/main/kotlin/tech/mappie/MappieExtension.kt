@file:Suppress("unused")

package tech.mappie

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

/**
 * Mode for normalizing property names during implicit matching.
 */
enum class NamingConvention {
    /** No normalization - property names must match exactly. */
    STRICT,
    /** Normalize names by lowercasing and removing separators (_ and -). */
    LENIENT,
}

abstract class MappieExtension(private val project: Project) {

    private val extensions = mutableMapOf<String, Any>()

    /**
     * Whether to report all warnings as errors.
     */
    abstract val warningsAsErrors: Property<Boolean>

    /**
     * Use default arguments if no mapping exists.
     */
    abstract val useDefaultArguments: Property<Boolean>

    /**
     * Naming convention for property matching.
     * STRICT: exact match required (default).
     * LENIENT: case-insensitive matching, ignores _ and - separators.
     */
    abstract val namingConvention: Property<NamingConvention>

    internal val strictness: MappieStrictnessExtension get() =
        extensions.getOrPut(MappieStrictnessExtension.NAME) {
            project.objects.newInstance(MappieStrictnessExtension::class.java)
        } as MappieStrictnessExtension

    internal val reporting: MappieReportingExtension get() =
        extensions.getOrPut(MappieReportingExtension.NAME) {
            project.objects.newInstance(MappieReportingExtension::class.java)
        } as MappieReportingExtension

    /**
     * Configuration options for the strictness of validations.
     */
    fun strictness(configuration: MappieStrictnessExtension.() -> Unit) {
        configuration(strictness)
    }

    /**
     * Configuration options for reporting.
     */
    fun reporting(configuration: MappieReportingExtension.() -> Unit) {
        configuration(reporting)
    }
}

abstract class MappieStrictnessExtension {

    /**
     * Whether to require all enum sources have a defined target.
     */
    abstract val enums: Property<Boolean>

    /**
     * Report a warning if a nullable platform type is used to assign to a non-nullable target.
     */
    abstract val platformTypeNullability: Property<Boolean>

    /**
     * Whether to require called elements to be visible from the current scope.
     */
    abstract val visibility: Property<Boolean>

    internal companion object {
        const val NAME = "mappie-strictness-extension"
    }
}


abstract class MappieReportingExtension {

    /**
     * Whether to generate reports.
     */
    abstract val enabled: Property<Boolean>

    /**
     * The location where to generate the report.
     */
    abstract val directory: DirectoryProperty

    internal companion object {
        const val NAME = "mappie-reporting-extension"
    }
}
