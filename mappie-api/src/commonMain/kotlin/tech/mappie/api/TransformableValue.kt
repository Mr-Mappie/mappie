@file:Suppress("UNUSED_PARAMETER")

package tech.mappie.api

/**
 * The result of an explicit mapping definition which can be transformed.
 */
public class TransformableValue<FROM, TO> {

    /**
     * Transforms the result value of a mapping.
     * See the [documentation](https://mappie.tech/object-mapping/the-transform-operator/)
     *
     * For example
     * ```kotlin
     * Person::age fromProperty PersonDto::dateOfBirth transform { it.periodUntil(Clock.todayIn(TimeZone.UTC)) }
     * ```
     * will generate an explicit mapping transforming `PersonDto.dateOfBirth` to the period between it and today.
     *
     * @param function the transformation function to transform the value with.
     */
    public infix fun transform(function: (FROM) -> TO): Unit = generated()

    /**
     * Transforms the result value of a mapping using a different mapper.
     * See the [documentation](https://mappie.tech/object-mapping/the-via-operator/)
     *
     * @param mapper the mapper to transform the value with.
     */
    public infix fun <M : Mappie<TO>> via(mapper: M): Unit = generated()
}
