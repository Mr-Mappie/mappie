package io.github.stefankoppier.mapping.annotations

import kotlin.reflect.KProperty1

abstract class EnumMapper<FROM: Enum<*>, TO : Enum<*>> : Mapper<FROM, TO>() {

    protected fun enumMapping(builder: EnumMapper<FROM, TO>.() -> Unit = { }): TO =
        error("Will be generated")
}

@Suppress("unused")
abstract class Mapper<FROM, TO> {

    abstract fun map(from: FROM): TO

    fun mapCollection(from: List<FROM>): List<TO> =
        from.map { map(it) }

    protected fun mapping(builder: Mapper<FROM, TO>.() -> Unit = { }): TO = generated()

    infix fun <TO_TYPE, FROM_TYPE> KProperty1<TO, TO_TYPE>.property(source: KProperty1<FROM, FROM_TYPE>): TransformableMapper<FROM_TYPE, TO_TYPE> = generated()

    infix fun KProperty1<TO, Byte>.constant(source: Byte): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, Short>.constant(source: Short): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, Int>.constant(source: Int): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, Long>.constant(source: Long): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, UByte>.constant(source: UByte): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, UShort>.constant(source: UShort): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, UInt>.constant(source: UInt): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, ULong>.constant(source: ULong): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, Float>.constant(source: Float): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, Double>.constant(source: Double): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, String>.constant(source: String): Mapper<FROM, TO> = generated()

    infix fun KProperty1<TO, Char>.constant(source: Char): Mapper<Int, Char> = generated()

    fun result(source: TO): Mapper<FROM, TO> = generated()
}

class TransformableMapper<FROM, TO> {

    infix fun transform(function: (FROM) -> TO): Unit = generated()
}

private fun generated(): Nothing =
    error("Will be generated")