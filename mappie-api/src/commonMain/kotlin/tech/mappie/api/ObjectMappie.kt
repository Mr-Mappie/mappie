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
public abstract class ObjectMappie<FROM, TO> : Mappie1<FROM, TO> {

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
     * @return An instance of the mapped target value.
     */
    protected fun mapping(builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun mapping(constructor: () -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param P1 The type of the first constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1> mapping(constructor: (P1) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2> mapping(constructor: (P1, P2) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     * 
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param P3 The type of the third constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2, P3> mapping(constructor: (P1, P2, P3) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     * 
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param P3 The type of the third constructor parameter.
     * @param P4 The type of the fourth constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2, P3, P4> mapping(constructor: (P1, P2, P3, P4) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     * 
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param P3 The type of the third constructor parameter.
     * @param P4 The type of the fourth constructor parameter.
     * @param P5 The type of the fifth constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2, P3, P4, P5> mapping(constructor: (P1, P2, P3, P4, P5) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     * 
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param P3 The type of the third constructor parameter.
     * @param P4 The type of the fourth constructor parameter.
     * @param P5 The type of the fifth constructor parameter.
     * @param P6 The type of the sixth constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2, P3, P4, P5, P6> mapping(constructor: (P1, P2, P3, P4, P5, P6) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param P3 The type of the third constructor parameter.
     * @param P4 The type of the fourth constructor parameter.
     * @param P5 The type of the fifth constructor parameter.
     * @param P6 The type of the sixth constructor parameter.
     * @param P7 The type of the seventh constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2, P3, P4, P5, P6, P7> mapping(constructor: (P1, P2, P3, P4, P5, P6, P7) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param P3 The type of the third constructor parameter.
     * @param P4 The type of the fourth constructor parameter.
     * @param P5 The type of the fifth constructor parameter.
     * @param P6 The type of the sixth constructor parameter.
     * @param P7 The type of the seventh constructor parameter.
     * @param P8 The type of the eighth constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2, P3, P4, P5, P6, P7, P8> mapping(constructor: (P1, P2, P3, P4, P5, P6, P7, P8) -> TO, builder: ObjectMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()
}
