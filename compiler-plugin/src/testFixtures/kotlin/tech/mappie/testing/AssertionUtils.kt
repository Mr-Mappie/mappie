package tech.mappie.testing

import org.assertj.core.api.AbstractStringAssert
import java.util.regex.Pattern

fun AbstractStringAssert<*>.containsError(message: String) =
    containsPattern(Pattern.compile("e: file://.+ $message"))
