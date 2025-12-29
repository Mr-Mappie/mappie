package tech.mappie.util

import org.jetbrains.kotlin.name.Name

/**
 * Normalizes a property name for case-insensitive matching.
 * Converts to lowercase and removes common separators (_, -).
 *
 * Examples:
 * - user_name -> username
 * - userName -> username
 * - user-name -> username
 * - UserName -> username
 */
fun Name.normalize(): String =
    asString().normalizePropertyName()

fun String.normalizePropertyName(): String =
    lowercase()
        .replace("_", "")
        .replace("-", "")
