package tech.mappie.config

data class MappieConfiguration(
    val modules: List<MappieModule>,
    val isMappieDebugMode: Boolean,
    val warningsAsErrors: Boolean,
    val useDefaultArguments: Boolean,
    val strictEnums: Boolean,
    val strictplatformTypeNullability: Boolean,
    val strictVisiblity: Boolean,
    val reportEnabled: Boolean,
    val reportDir: String,
)

enum class MappieModule { KOTLINX_DATETIME }