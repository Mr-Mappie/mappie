@file:Suppress("UNUSED_PARAMETER", "SameParameterValue")

package tech.mappie.api

/**
 * Base class for object mappers. See the [documentation](https://mappie.tech/object-mapping/object-mapping-overview/)
 * for a complete overview of how to generate object mappers.
 *
 * For example
 * ```kotlin
 * class PersonMapper : ObjectMappie2<PersonDto, AddressDto, Person>() {
 *      override fun map(first: PersonDto, second: AddressDto) = mapping()
 * }
 * ```
 * will generate a mapper from `PersonDto` and `AddressDto` to `Person`, assuming all dto's and `Person` have a resolvable mapping.
 *
 * @param FROM1 the first source type to map from.
 * @param FROM2 the second source type to map from.
 * @param TO the target type to map to.
 */
public abstract class ObjectMappie2<in FROM1, in FROM2, out TO> : Mappie<TO> {

    /**
     * Map [first] and [second] to an instance of [TO].
     *
     * @param first the first source value.
     * @param second the second source value.
     * @return [first] and [second] mapped to an instance of [TO].
     */
    public open fun map(first: FROM1, second: FROM2): TO = generated()

    /**
     * Map nullable [first] and [second] to an instance of [TO].
     *
     * @param first the first source value.
     * @param second the second source value.
     * @return [first] and [second] mapped an instance of [TO].
     */
    public open fun mapNullable(first: FROM1?, second: FROM2?): TO? =
        if (first == null || second == null) null else map(first, second)

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped value at runtime.
     */
    protected fun mapping(builder: MultipleObjectMappingConstructor<TO>.() -> Unit = { }): TO = generated()
}
