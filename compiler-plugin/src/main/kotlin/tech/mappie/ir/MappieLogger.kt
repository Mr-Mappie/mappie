package tech.mappie.ir

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import tech.mappie.ir.analysis.Problem

class MappieLogger(private val warningsAsErrors: Boolean, messageCollector: MessageCollector)
    : MessageCollector by messageCollector {

    fun logAll(problems: List<Problem>, location: CompilerMessageSourceLocation? = null) =
        problems.forEach { log(it, location) }

    fun log(problem: Problem, location: CompilerMessageSourceLocation? = null) =
        when (problem.severity) {
            Problem.Severity.ERROR -> error(messageOf(problem), problem.location ?: location)
            Problem.Severity.WARNING -> warn(messageOf(problem), problem.location ?: location)
        }

    fun logging(message: String, location: CompilerMessageSourceLocation? = null) =
        report(CompilerMessageSeverity.LOGGING, message, location)

    fun info(message: String, location: CompilerMessageSourceLocation? = null) =
        report(CompilerMessageSeverity.INFO, message, location)

    fun onlyWarn(message: String, location: CompilerMessageSourceLocation? = null) =
        report(CompilerMessageSeverity.WARNING, message, location)

    fun warn(message: String, location: CompilerMessageSourceLocation? = null) =
        if (warningsAsErrors) {
            report(CompilerMessageSeverity.ERROR, message, location)
        } else {
            report(CompilerMessageSeverity.WARNING, message, location)
        }

    fun error(message: String, location: CompilerMessageSourceLocation? = null) =
        report(CompilerMessageSeverity.ERROR, message, location)

    private fun messageOf(problem: Problem) =
        problem.description + System.lineSeparator() + problem.suggestions
            .mapIndexed { i, it -> i + 1 to it }
            .joinToString(separator = "") { "    ${it.first}. ${it.second}" + System.lineSeparator() }
}