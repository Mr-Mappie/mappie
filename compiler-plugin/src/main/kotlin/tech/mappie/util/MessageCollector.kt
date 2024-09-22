package tech.mappie.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.util.fileEntry

fun location(file: IrFileEntry, element: IrElement) =
    CompilerMessageLocation.create(
        file.name,
        file.getLineNumber(element.startOffset) + 1,
        file.getColumnNumber(element.startOffset) + 1,
        null,
    )

fun location(element: IrDeclaration) =
    location(element.fileEntry, element)
