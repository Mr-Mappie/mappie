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