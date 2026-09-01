package tech.mappie.config

import tech.mappie.config.options.NamingConventionMode

data class MappieConfiguration(
    val classpath: List<String>,
    val isMappieDebugMode: Boolean,
    val warningsAsErrors: Boolean,
    val useDefaultArguments: Boolean,
    val namingConvention: NamingConventionMode,
    val strictEnums: Boolean,
    val strictplatformTypeNullability: Boolean,
    val strictVisibility: Boolean,
    val reportEnabled: Boolean,
    val outputDir: String?,
    val reportDir: String,
)
