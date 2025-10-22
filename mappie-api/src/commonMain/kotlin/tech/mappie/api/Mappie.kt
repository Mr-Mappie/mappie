package tech.mappie.api

public sealed interface Mappie1<in FROM, out TO> : Mappie<TO> {

    /**
     * Map [from] to an instance of [TO].
     *
     * @param from the source value.
     * @return [from] mapped to an instance of [TO].
     */
    public fun map(from: FROM): TO = generated()
}

public sealed interface Mappie<out TO>

internal fun generated(): Nothing =
    error("Will be generated")