package tech.mappie.config

data class MappieConfiguration(
    val isMappieDebugMode: Boolean,
    val warningsAsErrors: Boolean,
    val useDefaultArguments: Boolean,
    val strictEnums: Boolean,
    val strictVisiblity: Boolean,
    val reportEnabled: Boolean,
    val reportDir: String,
)