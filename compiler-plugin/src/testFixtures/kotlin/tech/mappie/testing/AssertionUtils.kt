package tech.mappie.testing

import org.assertj.core.api.AbstractStringAssert
import java.util.regex.Pattern
import kotlin.text.Regex.Companion.escape

fun AbstractStringAssert<*>.containsError(message: String, suggestions: List<String> = emptyList()): AbstractStringAssert<*> =
    containsPattern(Pattern.compile("e: file://.+ ${escape(messageOf(message, suggestions))}"))

fun AbstractStringAssert<*>.containsWarning(message: String): AbstractStringAssert<*> =
    containsPattern(Pattern.compile("w: file://.+ ${escape(message)}"))

private fun messageOf(message: String, suggestions: List<String>) =
    message + System.lineSeparator() + suggestions
        .mapIndexed { i, it -> i + 1 to it }
        .joinToString(separator = "") { "    ${it.first}. ${it.second}" + System.lineSeparator() }