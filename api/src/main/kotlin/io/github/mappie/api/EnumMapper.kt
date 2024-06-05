@file:Suppress("unused")

package io.github.mappie.api

abstract class EnumMapper<FROM: Enum<*>, TO : Enum<*>> : Mapper<FROM, TO>() {

    protected infix fun TO.mappedFromEnumEntry(source: FROM): EnumMapper<FROM, TO> = generated()
}