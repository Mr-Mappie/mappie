package tech.mappie.validation

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation

data class Problem(
    val description: String,
    val severity: Severity,
    val location: CompilerMessageLocation?
) {
    enum class Severity { ERROR, WARNING; }

    companion object {
        fun error(description: String, location: CompilerMessageLocation? = null) =
            Problem(description, Severity.ERROR, location)

        fun warning(description: String, location: CompilerMessageLocation? = null) =
            Problem(description, Severity.WARNING, location)
    }
}