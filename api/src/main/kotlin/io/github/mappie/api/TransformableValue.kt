@file:Suppress("UNUSED_PARAMETER")

package io.github.mappie.api

/**
 * The result of an explicit mapping definition which can be transformed.
 */
class TransformableValue<FROM, TO> {

    /**
     * Transforms the result value of a mapping.
     * See the [documentation](https://mappie.tech/object-mapping/transforming/)
     *
     * For example
     * ```kotlin
     * Person::age mappedFromProperty PersonDto::dateOfBirth transform { it.periodUntil(Clock.todayIn(TimeZone.UTC)) }
     * ```
     * will generate an explicit mapping transforming `PersonDto.dateOfBirth` to the period between it and today.
     *
     * @param function the transformation function to transform the value with.
     */
    infix fun transform(function: (FROM) -> TO): Unit = generated()

    /**
     * Transforms the result value of a mapping using a different mapper.
     * See the [documentation](https://mappie.tech/object-mapping/reusing-mappers/)
     *
     * @param mapper the mapper to transform the value with.
     */
    infix fun <M : Mapper<FROM, TO>> via(mapper: M): M = generated()
}
