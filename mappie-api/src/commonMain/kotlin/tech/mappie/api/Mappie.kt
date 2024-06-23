@file:Suppress("unused", "UNUSED_PARAMETER")

package tech.mappie.api

public abstract class Mappie<FROM, TO> {

    /**
     * Map [from] to an instance of [TO].
     *
     * @param from the source value.
     * @return [from] mapped to an instance of [TO].
     */
    public open fun map(from: FROM): TO = generated()

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
    protected fun mapping(builder: Mappie<FROM, TO>.() -> Unit = { }): TO = generated()
}

internal fun generated(): Nothing =
    error("Will be generated")