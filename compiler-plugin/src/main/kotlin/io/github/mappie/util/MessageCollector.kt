package io.github.mappie.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.util.fileEntry

fun MessageCollector.info(message: String) = report(CompilerMessageSeverity.INFO, message)

fun MessageCollector.warn(message: String, location: CompilerMessageSourceLocation? = null) =
    report(CompilerMessageSeverity.WARNING, message, location)

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

