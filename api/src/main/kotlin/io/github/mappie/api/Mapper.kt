@file:Suppress("unused")

package io.github.mappie.api

abstract class Mapper<FROM, TO> {

    abstract fun map(from: FROM): TO

    fun mapCollection(from: List<FROM>): List<TO> =
        from.map { map(it) }

    protected fun mapping(builder: Mapper<FROM, TO>.() -> Unit = { }): TO = generated()
}

internal fun generated(): Nothing =
    error("Will be generated")