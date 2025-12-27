package tech.mappie.config

import java.util.*

data class MappieConfiguration(
    val modules: EnumSet<MappieModule>,
    val isMappieDebugMode: Boolean,
    val warningsAsErrors: Boolean,
    val useDefaultArguments: Boolean,
    val useCaseInsensitiveMatching: Boolean,
    val strictEnums: Boolean,
    val strictplatformTypeNullability: Boolean,
    val strictVisibility: Boolean,
    val reportEnabled: Boolean,
    val reportDir: String,
)

enum class MappieModule { KOTLINX_DATETIME, KOTLINX_COLLECTIONS_IMMUTABLE }