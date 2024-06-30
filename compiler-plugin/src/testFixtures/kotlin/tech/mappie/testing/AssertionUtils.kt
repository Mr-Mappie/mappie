package tech.mappie.testing

import org.assertj.core.api.AbstractStringAssert
import java.util.regex.Pattern

fun AbstractStringAssert<*>.containsError(message: String): AbstractStringAssert<*> =
    containsPattern(Pattern.compile("e: file://.+ $message"))

fun AbstractStringAssert<*>.containsWarning(message: String): AbstractStringAssert<*> =
    containsPattern(Pattern.compile("w: file://.+ $message"))
