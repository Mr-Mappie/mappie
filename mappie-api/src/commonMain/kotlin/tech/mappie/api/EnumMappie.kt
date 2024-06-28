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
public abstract class EnumMappie<FROM: Enum<*>, TO : Enum<*>> : Mappie<FROM, TO>() {

    /**
     * Explicitly construct a mapping to [TO] from source entry [source].
     *
     * For example
     * ```kotlin
     * Colour.UNKNOWN fromEnumEntry Color.ORANGE
     * ```
     * will generate an explicit mapping, mapping `Colour.ORANGE` to `Color.UNKNOWN`.
     */
    protected infix fun TO.fromEnumEntry(source: FROM): Unit = generated()

    /**
     * Explicitly construct a mapping to throw an exception from source entry [source].
     *
     * For example
     * ```kotlin
     * IllegalStateException() fromEnumEntry Color.ORANGE
     * ```
     * will generate an explicit mapping, mapping `Colour.ORANGE` to an [IllegalStateException] being thrown.
     */
    protected infix fun Throwable.thrownByEnumEntry(source: FROM): Unit = generated()
}