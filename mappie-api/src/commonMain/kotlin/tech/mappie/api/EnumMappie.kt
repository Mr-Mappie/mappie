@file:Suppress("unused", "UNUSED_PARAMETER")

package tech.mappie.api

/**
 * Base class for enum mappers. See the [documentation](https://mappie.tech/enum-mapping/enum-mapping-overview/)
 * for a complete overview of how to generate enum mappers.
 *
 * For example
 * ```kotlin
 * class PersonMapper : EnumMappie<Color, Colour>() {
 *      override fun map(from: Color) = mapping()
 * }
 * ```
 * will generate a mapper from `Color` to `Colour`, assuming both `Color` and `Colour` have a resolvable mapping.
 *
 * @param FROM the source type to map from.
 * @param TO the target type to map to.
 */
public abstract class EnumMappie<FROM: Enum<*>, TO> : Mappie<TO> {

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
        if (from == null) null else map(from)

    /**
     * Map each element in [from] to an instance of [TO].
     *
     * @param from the source values.
     * @return [from] mapped to a list of instances of [TO].
     */
    public open fun mapList(from: List<FROM>): List<TO> =
        ArrayList<TO>(from.size).apply { from.forEach { add(map(it)) } }

    /**
     * Map each element in [from] to an instance of [TO].
     *
     * @param from the source values.
     * @return [from] mapped to a set of instances of [TO].
     */
    public open fun mapSet(from: Set<FROM>): Set<TO> =
        HashSet<TO>(from.size).apply { from.forEach { add(map(it)) } }

    /**
     * Mapping function which instructs Mappie to generate code for this implementation.
     *
     * @param builder the configuration for the generation of this mapping.
     * @return An instance of the mapped value at runtime.
     */
    protected fun mapping(builder: EnumMappingConstructor<FROM, TO>.() -> Unit = { }): TO = generated()
}

public class EnumMappingConstructor<FROM, TO> {

    /**
     * Explicitly construct a mapping to [TO] from source entry [source].
     *
     * For example
     * ```kotlin
     * Colour.UNKNOWN fromEnumEntry Color.ORANGE
     * ```
     * will generate an explicit mapping, mapping `Colour.ORANGE` to `Color.UNKNOWN`.
     */
    public infix fun TO.fromEnumEntry(source: FROM): Unit = generated()

    /**
     * Explicitly construct a mapping to throw an exception from source entry [source].
     *
     * For example
     * ```kotlin
     * IllegalStateException() fromEnumEntry Color.ORANGE
     * ```
     * will generate an explicit mapping, mapping `Colour.ORANGE` to an [IllegalStateException] being thrown.
     */
    public infix fun Throwable.thrownByEnumEntry(source: FROM): Unit = generated()
}