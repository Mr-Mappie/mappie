@file:Suppress("UNUSED_PARAMETER", "SameParameterValue")

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
public abstract class ObjectMappie<FROM, out TO> : Mappie<TO> {

    /**
     * A mapper for [List] to be used in [TransformableValue.via].
     */
    public val forList: ListMappie<TO> get() =
        error("The mapper forList should only be used in the context of 'via'. Use mapList instead.")

    /**
     * A mapper for [Set] to be used in [TransformableValue.via].
     */
    public val forSet: SetMappie<TO> get() =
        error("The mapper forSet should only be used in the context of 'via'. Use mapSet instead.")

    /**
     * Map [from] to an instance of [TO].
     *
     * @param from the source value.
     * @return [from] mapped to an instance of [TO].
     */
    public open fun map(from: FROM): TO = generated()

    /**
     * Map nullable [from] to an instance of [TO].
     *
     * @param from the source value.
     * @return [from] mapped to an instance of [TO].
     */
    public fun mapNullable(from: FROM?): TO? =
        if (from == null) null else map(from)

    /**
     * Map each element in [from] to an instance of [TO].
     *
     * @param from the source values.
     * @return [from] mapped to a list of instances of [TO].
     */
    public fun mapList(from: List<FROM>): List<TO> =
        ArrayList<TO>(from.size).apply { from.forEach { add(map(it)) } }

    /**
     * Map each element in [from] to an instance of [TO].
     *
     * @param from the source values.
     * @return [from] mapped to a set of instances of [TO].
     */
    public fun mapSet(from: Set<FROM>): Set<TO> =
        HashSet<TO>(from.size).apply { from.forEach { add(map(it)) } }

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped value at runtime.
     */
    protected fun mapping(builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()
}

public class ObjectMappingConstructor<FROM, out TO> {

    /**
     * Alias for the target type [TO] to simply property references.
     *
     * For example, suppose we are constructing a mapper with target type `Person`
     * ```kotlin
     *  to::name fromProperty PersonDto::fullName
     * ```
     * is equivalent to `Person::name fromProperty PersonDto::fullName`.
     */
    public val to: TO
        get() = error("The to property should only be used in the context of `to::property fromX y`.")

    /**
     * Explicitly construct a mapping to [TO] from property source [source].
     *
     * For example
     * ```kotlin
     * Person::name fromProperty PersonDto::fullName
     * ```
     * will generate an explicit mapping, setting constructor parameter `Person.name` to `PersonDto.fullName`.
     */
    public infix fun <TO_TYPE, FROM_TYPE> KProperty<TO_TYPE>.fromProperty(source: KProperty<FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
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
    public infix fun <TO_TYPE> KProperty<TO_TYPE>.fromValue(value: TO_TYPE): Unit =
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
    public infix fun <FROM_TYPE, TO_TYPE> KProperty<TO_TYPE>.fromExpression(function: (FROM) -> FROM_TYPE): Unit =
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
    public fun parameter(name: String): KProperty<*> =
        generated()
}