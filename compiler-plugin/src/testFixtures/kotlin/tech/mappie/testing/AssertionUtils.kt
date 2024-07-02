package tech.mappie.testing

import org.assertj.core.api.AbstractStringAssert
import java.util.regex.Pattern
import kotlin.text.Regex.Companion.escape

fun AbstractStringAssert<*>.containsError(message: String): AbstractStringAssert<*> =
    containsPattern(Pattern.compile("e: file://.+ ${escape(message)}"))

fun AbstractStringAssert<*>.containsWarning(message: String): AbstractStringAssert<*> =
    containsPattern(Pattern.compile("w: file://.+ ${escape(message)}"))
