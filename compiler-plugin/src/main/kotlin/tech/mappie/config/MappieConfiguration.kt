package tech.mappie.config

import org.jetbrains.kotlin.config.CompilerConfiguration
import tech.mappie.config.options.NamingConventionMode
import java.util.EnumSet

data class MappieConfiguration(
    val configuration: CompilerConfiguration,
    val modules: EnumSet<MappieModule>,
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

enum class MappieModule { KOTLINX_DATETIME, KOTLINX_COLLECTIONS_IMMUTABLE }
