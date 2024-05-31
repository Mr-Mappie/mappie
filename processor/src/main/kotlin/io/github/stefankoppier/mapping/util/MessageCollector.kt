package io.github.stefankoppier.mapping.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

fun MessageCollector.info(message: String) = report(CompilerMessageSeverity.INFO, message)

fun MessageCollector.warn(message: String) = report(CompilerMessageSeverity.WARNING, message)

fun MessageCollector.error(message: String) = report(CompilerMessageSeverity.ERROR, message)