package io.github.stefankoppier.mapping.annotations

import kotlin.reflect.KProperty1

@Suppress("unused")
abstract class Mapper<FROM, TO> {

    abstract fun map(from: FROM): TO

    fun mapCollection(from: List<FROM>): List<TO> =
        from.map { map(it) }

    protected fun mapping(builder: Mapper<FROM, TO>.() -> Unit = { }): TO =
        error("Will be generated")

    infix fun <FROM_TYPE, TO_TYPE> KProperty1<FROM, FROM_TYPE>.mappedTo(target: KProperty1<TO, TO_TYPE>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun Byte.constant(target: KProperty1<TO, Byte>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun Short.constant(target: KProperty1<TO, Short>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun Int.constant(target: KProperty1<TO, Int>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun Long.constant(target: KProperty1<TO, Long>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun UByte.constant(target: KProperty1<TO, UByte>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun UShort.constant(target: KProperty1<TO, UShort>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun UInt.constant(target: KProperty1<TO, UInt>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun ULong.constant(target: KProperty1<TO, ULong>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun Float.constant(target: KProperty1<TO, Float>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun Double.constant(target: KProperty1<TO, Double>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun String.constant(target: KProperty1<TO, String>): Mapper<FROM, TO> =
        error("Will be generated")

    infix fun Char.constant(target: KProperty1<TO, Char>): Mapper<Int, Char> =
        error("Will be generated")
}