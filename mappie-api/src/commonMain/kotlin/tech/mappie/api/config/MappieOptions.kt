package tech.mappie.api.config

@Target(AnnotationTarget.CLASS)
public annotation class UseDefaultArguments(val value: Boolean = true)

@Target(AnnotationTarget.CLASS)
public annotation class UseStrictEnums(val value: Boolean = true)

@Target(AnnotationTarget.CLASS)
public annotation class UseStrictPlatformTypeNullabilityValidation(val value: Boolean = true)

@Target(AnnotationTarget.CLASS)
public annotation class UseStrictVisibility(val value: Boolean = true)

@Target(AnnotationTarget.FUNCTION)
public annotation class ExcludeFromMapping

/**
 * Mode for normalizing property names during implicit matching.
 */
public enum class NamingConvention {
    /** No normalization - property names must match exactly. */
    STRICT,
    /** Normalize names by lowercasing and removing separators (_ and -). */
    LENIENT,
}

/**
 * Configure the naming convention used for implicit property matching.
 *
 * When set to [NamingConvention.LENIENT], property names are normalized by converting to lowercase
 * and removing separators (`_` and `-`), allowing matches like `user_name` to `userName`.
 */
@Target(AnnotationTarget.CLASS)
public annotation class UseNamingConvention(val value: NamingConvention = NamingConvention.LENIENT)