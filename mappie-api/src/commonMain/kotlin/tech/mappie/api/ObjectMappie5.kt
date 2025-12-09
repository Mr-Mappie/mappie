@file:Suppress("UNUSED_PARAMETER", "SameParameterValue")

package tech.mappie.api

/**
 * Base class for object mappers. See the [documentation](https://mappie.tech/object-mapping/object-mapping-overview/)
 * for a complete overview of how to generate object mappers.
 *
 * @param FROM1 the first source type to map from.
 * @param FROM2 the second source type to map from.
 * @param FROM3 the third source type to map from.
 * @param FROM4 the fourth source type to map from.
 * @param FROM5 the fifth source type to map from.
 * @param TO the target type to map to.
 */
public abstract class ObjectMappie5<FROM1, FROM2, FROM3, FROM4, FROM5, TO> : Mappie<TO> {

    /**
     * Map [first], [second], [third], [fourth], and [fifth] to an instance of [TO].
     *
     * @param first the first source value.
     * @param second the second source value.
     * @param third the third source value.
     * @param fourth the fourth source value.
     * @param fifth the fifth source value.
     * @return [first], [second] [third], [fourth], and [fifth] mapped to an instance of [TO].
     */
    public open fun map(first: FROM1, second: FROM2, third: FROM3, fourth: FROM4, fifth: FROM5): TO = generated()

    /**
     * Map nullable [first], [second], [third], [fourth], and [fifth] to an instance of [TO].
     *
     * @param first the first source value.
     * @param second the second source value.
     * @param third the third source value.
     * @param fourth the fourth source value.
     * @param fifth the fifth source value.
     * @return [first], [second], [third], [fourth], and [fifth] mapped an instance of [TO].
     */
    public open fun mapNullable(first: FROM1?, second: FROM2?, third: FROM3?, fourth: FROM4?, fifth: FROM5?): TO? =
        if (first == null || second == null || third == null || fourth == null || fifth == null) null else map(first, second, third, fourth, fifth)

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun mapping(builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun mapping(constructor: () -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param P1 The type of the first constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1> mapping(constructor: (P1) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param P1 The type of the first constructor parameter.
     * @param P2 The type of the second constructor parameter.
     * @param constructor The specific constructor to call, e.g `::TargetClass`.
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped target value.
     */
    protected fun <P1, P2> mapping(constructor: (P1, P2) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

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
    protected fun <P1, P2, P3> mapping(constructor: (P1, P2, P3) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

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
    protected fun <P1, P2, P3, P4> mapping(constructor: (P1, P2, P3, P4) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

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
    protected fun <P1, P2, P3, P4, P5> mapping(constructor: (P1, P2, P3, P4, P5) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

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
    protected fun <P1, P2, P3, P4, P5, P6> mapping(constructor: (P1, P2, P3, P4, P5, P6) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

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
    protected fun <P1, P2, P3, P4, P5, P6, P7> mapping(constructor: (P1, P2, P3, P4, P5, P6, P7) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()

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
    protected fun <P1, P2, P3, P4, P5, P6, P7, P8> mapping(constructor: (P1, P2, P3, P4, P5, P6, P7, P8) -> TO, builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()
}
