package tech.mappie.config

data class MappieConfiguration(
    val warningsAsErrors: Boolean,
    val useDefaultArguments: Boolean,
    val strictness: StrictnessConfiguration,
)

data class StrictnessConfiguration(
    val enums: Boolean,
    val visibility: Boolean,
)