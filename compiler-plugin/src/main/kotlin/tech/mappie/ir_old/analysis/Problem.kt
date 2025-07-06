package tech.mappie.ir_old.analysis

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation

data class Problem(
    val description: String,
    val severity: Severity,
    val suggestions: List<String>,
    val location: CompilerMessageLocation?
) {
    enum class Severity { ERROR, WARNING; }

    companion object {
        fun error(description: String, location: CompilerMessageLocation? = null, suggestions: List<String> = emptyList()) =
            Problem(description, Severity.ERROR, suggestions, location)

        fun warning(description: String, location: CompilerMessageLocation? = null, suggestions: List<String> = emptyList()) =
            Problem(description, Severity.WARNING, suggestions, location)
    }
}