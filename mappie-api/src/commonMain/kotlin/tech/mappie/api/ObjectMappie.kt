@file:Suppress("unused", "SameParameterValue")

package tech.mappie.api

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
    public open fun mapNullable(from: FROM?): TO? =
        from?.let(::map)

    /**
     * Map each element in [from] to an instance of [TO].
     *
     * @param from the source values.
     * @return [from] mapped to a list of instances of [TO].
     */
    public open fun mapList(from: List<FROM>): List<TO> =
        ArrayList<TO>(from.size).apply { from.forEach { add(map(it)) } }

    /**
     * Map each element in [from] to an instance of [TO] if [from] is not null.
     *
     * @param from the source values.
     * @return [from] mapped to a list of instances of [TO].
     */
    public open fun mapNullableList(from: List<FROM>?): List<TO>? =
        from?.let(::mapList)

    /**
     * Map each element in [from] to an instance of [TO].
     *
     * @param from the source values.
     * @return [from] mapped to a set of instances of [TO].
     */
    public open fun mapSet(from: Set<FROM>): Set<TO> =
        HashSet<TO>(from.size).apply { from.forEach { add(map(it)) } }

    /**
     * Map each element in [from] to an instance of [TO] if [from] is not null.
     *
     * @param from the source values.
     * @return [from] mapped to a set of instances of [TO].
     */
    public open fun mapNullableSet(from: Set<FROM>?): Set<TO>? =
        from?.let(::mapSet)

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped value at runtime.
     */
    protected fun mapping(builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()
}
