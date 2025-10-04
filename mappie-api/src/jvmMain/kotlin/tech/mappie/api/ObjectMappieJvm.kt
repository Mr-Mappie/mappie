package tech.mappie.api

/**
 * Map each element in [from] to an instance of [TO].
 *
 * @param from the source values.
 * @return [from] mapped to an array of instances of [TO].
 */
public inline fun <FROM, reified TO> ObjectMappie<FROM, TO>.mapArray(from: Array<FROM>): Array<TO> =
    Array(from.size) { map(from[it]) }
