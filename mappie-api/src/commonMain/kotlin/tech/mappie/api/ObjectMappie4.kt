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
 * @param TO the target type to map to.
 */
public abstract class ObjectMappie4<in FROM1, in FROM2, in FROM3, in FROM4, out TO> : Mappie<TO> {

    /**
     * Map [first], [second], [third], and [fourth] to an instance of [TO].
     *
     * @param first the first source value.
     * @param second the second source value.
     * @param third the third source value.
     * @param fourth the fourth source value.
     * @return [first], [second] [third] and [fourth] mapped to an instance of [TO].
     */
    public open fun map(first: FROM1, second: FROM2, third: FROM3, fourth: FROM4): TO = generated()

    /**
     * Map nullable [first], [second], [third], and [fourth] to an instance of [TO].
     *
     * @param first the first source value.
     * @param second the second source value.
     * @param third the third source value.
     * @param fourth the fourth source value.
     * @return [first], [second], [third], and [fourth] mapped an instance of [TO].
     */
    public fun mapNullable(first: FROM1?, second: FROM2?, third: FROM3?, fourth: FROM4?): TO? =
        if (first == null || second == null || third == null || fourth == null ) null else map(first, second, third, fourth)

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped value at runtime.
     */
    protected fun mapping(builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()
}
