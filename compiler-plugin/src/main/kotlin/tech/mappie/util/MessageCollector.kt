package tech.mappie.util

import tech.mappie.MappieIrRegistrar.Companion.context
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.validation.Problem

fun logAll(problems: List<Problem>, location: CompilerMessageSourceLocation? = null) =
    problems.forEach { log(it, location) }

fun log(problem: Problem, location: CompilerMessageSourceLocation?) =
    when(problem.severity) {
        Problem.Severity.ERROR -> logError(problem.description, problem.location ?: location)
        Problem.Severity.WARNING -> logWarn(problem.description, problem.location ?: location)
    }

fun logInfo(message: String, location: CompilerMessageSourceLocation? = null) =
    context.messageCollector.info(message, location)

fun logWarn(message: String, location: CompilerMessageSourceLocation? = null) =
    context.messageCollector.warn(message, location)

fun logError(message: String, location: CompilerMessageSourceLocation? = null) =
    context.messageCollector.error(message, location)

fun MessageCollector.info(message: String, location: CompilerMessageSourceLocation? = null) =
    report(CompilerMessageSeverity.INFO, message, location)

fun MessageCollector.warn(message: String, location: CompilerMessageSourceLocation? = null) =
    if (context.configuration.warningsAsErrors) {
        report(CompilerMessageSeverity.ERROR, message, location)
    } else {
        report(CompilerMessageSeverity.WARNING, message, location)
    }

fun MessageCollector.error(message: String, location: CompilerMessageSourceLocation? = null) =
    report(CompilerMessageSeverity.ERROR, message, location)

fun location(file: IrFileEntry, element: IrElement) =
    CompilerMessageLocation.create(
        file.name,
        file.getLineNumber(element.startOffset) + 1,
        file.getColumnNumber(element.startOffset) + 1,
        null,
    )

fun location(element: IrDeclaration) =
    location(element.fileEntry, element)

