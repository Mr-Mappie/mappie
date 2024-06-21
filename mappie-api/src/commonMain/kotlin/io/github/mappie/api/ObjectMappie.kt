@file:Suppress("unused", "UNUSED_PARAMETER", "SameParameterValue")

package io.github.mappie.api

import kotlin.reflect.KProperty1

/**
 * Base class for object mappers. See the [documentation](https://mappie.tech/object-mapping/object-mapping-overview/)
 * for a complete overview of how to generate object mappers.
 *
 * For example
 * ```kotlin
 * class PersonMapper : ObjectMappie<PersonDto, Person>() {
 *      override fun map(from: PersonDto) = mapping()
 * }
 * ```
 * will generate a mapper from `PersonDto` to `Person`, assuming both `PersonDto` and `Person` have a resolvable mapping.
 *
 * @param FROM the source type to map from.
 * @param TO the target type to map to.
 */
public abstract class ObjectMappie<FROM, TO> : Mappie<FROM, TO>() {

    /**
     * A mapper for [List] to be used in [TransformableValue.via].
     */
    public val forList: ListMappie<FROM, TO> get() =
        error("The mapper forList should only be used in the context of 'via'. Use mapList instead.")

    /**
     * A mapper for [Set] to be used in [TransformableValue.via].
     */
    public val forSet: SetMappie<FROM, TO> get() =
        error("The mapper forSet should only be used in the context of 'via'. Use mapSet instead.")

    /**
     * Explicitly construct a mapping to [TO] from property source [source].
     *
     * For example
     * ```kotlin
     * Person::name mappedFromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `PersonDto.fullName`.
     */
    protected infix fun <TO_TYPE, FROM_TYPE> KProperty1<TO, TO_TYPE>.mappedFromProperty(source: KProperty1<FROM, FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from constant source [value].
     *
     * For example
     * ```kotlin
     * Person::name mappedFromConstant "John Doe"
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe"`.
     */
    protected infix fun <TO_TYPE> KProperty1<TO, TO_TYPE>.mappedFromConstant(value: TO_TYPE): Unit =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from expression source [function].
     *
     * For example
     * ```kotlin
     * Person::name mappedFromConstant { personDto -> personDto.fullName + " (full)" }
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe (full)"`,
     * assuming `personDto.fullName == "John Doe"`.
     */
    protected infix fun <TO_TYPE> KProperty1<TO, TO_TYPE>.mappedFromExpression(function: (FROM) -> TO_TYPE): Unit =
        generated()

    /**
     * Reference a constructor parameter in lieu of a property reference, if it not exists as a property.
     *
     * For example
     * ```kotlin
     * parameter("name") mappedFromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `name` to `PersonDto.fullName`.
     */
    protected fun parameter(name: String): KProperty1<TO, *> =
        generated()
}