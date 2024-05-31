package io.github.stefankoppier.mapping.annotations

import kotlin.reflect.KProperty1

abstract class Mapper<FROM, TO> {
    abstract fun map(from: FROM): TO

    protected fun mapping(builder: Mapper<FROM, TO>.() -> Unit = { }): TO =
        error("Will be generated")

    protected infix fun <FROM_TYPE, TO_TYPE> KProperty1<FROM, FROM_TYPE>.mappedTo(rhs: KProperty1<TO, TO_TYPE>): Mapper<FROM, TO> =
        error("Will be generated")
}