package tech.mappie.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.MappieContext
import tech.mappie.validation.Problem

fun MappieContext.logAll(problems: List<Problem>, location: CompilerMessageSourceLocation? = null) =
    problems.forEach { log(it, location) }

fun MappieContext.log(problem: Problem, location: CompilerMessageSourceLocation? = null) =
    when (problem.severity) {
        Problem.Severity.ERROR -> logError(messageOf(problem), problem.location ?: location)
        Problem.Severity.WARNING -> logWarn(messageOf(problem), problem.location ?: location)
    }

fun MappieContext.logInfo(message: String, location: CompilerMessageSourceLocation? = null) =
    info(message, location)

fun MappieContext.logWarn(message: String, location: CompilerMessageSourceLocation? = null) =
    warn(message, location)

fun MappieContext.logError(message: String, location: CompilerMessageSourceLocation? = null) =
    error(message, location)

fun MappieContext.info(message: String, location: CompilerMessageSourceLocation? = null) =
    reporter.report(CompilerMessageSeverity.INFO, message, location)

fun MappieContext.warn(message: String, location: CompilerMessageSourceLocation? = null) =
    if (configuration.warningsAsErrors) {
        reporter.report(CompilerMessageSeverity.ERROR, message, location)
    } else {
        reporter.report(CompilerMessageSeverity.WARNING, message, location)
    }

fun MappieContext.error(message: String, location: CompilerMessageSourceLocation? = null) =
    reporter.report(CompilerMessageSeverity.ERROR, message, location)

fun location(file: IrFileEntry, element: IrElement) =
    CompilerMessageLocation.create(
        file.name,
        file.getLineNumber(element.startOffset) + 1,
        file.getColumnNumber(element.startOffset) + 1,
        null,
    )

fun location(element: IrDeclaration) =
    location(element.fileEntry, element)

private fun messageOf(problem: Problem) =
    problem.description + System.lineSeparator() + problem.suggestions
        .mapIndexed { i, it -> i + 1 to it }
        .joinToString(separator = "") { "    ${it.first}. ${it.second}" + System.lineSeparator() }