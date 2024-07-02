@file:Suppress("unused", "UNUSED_PARAMETER", "SameParameterValue")

package tech.mappie.api

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
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
     * Alias for the target type [TO] to simply property references.
     * For example, in the following code
     * ```kotlin
     * object Mapper : ObjectMappie<Person, PersonDto>() {
     *     override fun map(from: Person) = mapping {
     *         to::name fromProperty PersonDto::fullName
     *     }
     * }
     * ```
     * the line `to::name fromProperty PersonDto::fullName` is equivalent to
     * `Person::name fromProperty PersonDto::fullName`.
     */
    protected val to: TO
        get() = error("The to property should only be used in the context of `to::property fromX y`.")

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
     * Person::name fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `PersonDto.fullName`.
     */
    protected infix fun <TO_TYPE, FROM_TYPE> KProperty<TO_TYPE>.fromProperty(source: KProperty0<FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from property source [source].
     *
     * For example
     * ```kotlin
     * Person::name fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `PersonDto.fullName`.
     */
    protected infix fun <TO_TYPE, FROM_TYPE> KProperty<TO_TYPE>.fromProperty(source: KProperty1<FROM, FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from constant source [value].
     *
     * For example
     * ```kotlin
     * Person::name fromConstant "John Doe"
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe"`.
     */
    @Deprecated("This function is unnecessarily limiting.", replaceWith = ReplaceWith("this fromValue value"))
    protected infix fun <TO_TYPE> KProperty<TO_TYPE>.fromConstant(value: TO_TYPE): Unit =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from a value source [value].
     *
     * For example
     * ```kotlin
     * Person::name fromValue "John Doe"
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe"`.
     */
    protected infix fun <TO_TYPE> KProperty<TO_TYPE>.fromValue(value: TO_TYPE): Unit =
        generated()

    /**
     * Explicitly construct a mapping to [TO] from expression source [function].
     *
     * For example
     * ```kotlin
     * Person::name fromExpression { personDto -> personDto.fullName + " (full)" }
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `"John Doe (full)"`,
     * assuming `personDto.fullName == "John Doe"`.
     */
    protected infix fun <FROM_TYPE, TO_TYPE> KProperty<TO_TYPE>.fromExpression(function: (FROM) -> FROM_TYPE): Unit =
        generated()

    /**
     * Reference a constructor parameter in lieu of a property reference, if it not exists as a property.
     *
     * For example
     * ```kotlin
     * parameter("name") fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `name` to `PersonDto.fullName`.
     */
    protected fun parameter(name: String): KProperty1<TO, *> =
        generated()
}