package tech.mappie.ir.analysis

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation

data class Problem(
    val description: String,
    val severity: Severity,
    val suggestions: List<String>,
    val location: CompilerMessageLocation?
) {
    enum class Severity { ERROR, WARNING; }

    companion object {
        private const val ISSUES_URL = "https://github.com/Mr-Mappie/mappie/issues"

        fun exception(description: String): Exception =
            IllegalStateException("Internal Mappie error: $description. Please report this bug at $ISSUES_URL.")

        fun internal(description: String) =
            Problem("Internal Mappie error: $description", Severity.ERROR, listOf("Please report this bug at $ISSUES_URL."), null)

        fun error(description: String, location: CompilerMessageLocation? = null, suggestions: List<String> = emptyList()) =
            Problem(description, Severity.ERROR, suggestions, location)

        fun warning(description: String, location: CompilerMessageLocation? = null, suggestions: List<String> = emptyList()) =
            Problem(description, Severity.WARNING, suggestions, location)
    }
}