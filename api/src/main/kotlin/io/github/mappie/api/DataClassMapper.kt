@file:Suppress("unused")

package io.github.mappie.api

import kotlin.reflect.KProperty1

abstract class DataClassMapper<FROM, TO> : Mapper<FROM, TO>() {
    protected infix fun <TO_TYPE, FROM_TYPE> KProperty1<TO, TO_TYPE>.mappedFromProperty(source: KProperty1<FROM, FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
        generated()

    protected infix fun KProperty1<TO, Byte>.mappedFromConstant(source: Byte): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, Short>.mappedFromConstant(source: Short): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, Int>.mappedFromConstant(source: Int): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, Long>.mappedFromConstant(source: Long): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, UByte>.mappedFromConstant(source: UByte): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, UShort>.mappedFromConstant(source: UShort): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, UInt>.mappedFromConstant(source: UInt): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, ULong>.mappedFromConstant(source: ULong): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, Float>.mappedFromConstant(source: Float): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, Double>.mappedFromConstant(source: Double): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, String>.mappedFromConstant(source: String): DataClassMapper<FROM, TO> = generated()

    protected infix fun KProperty1<TO, Char>.mappedFromConstant(source: Char): DataClassMapper<Int, Char> = generated()

    protected fun result(source: TO): DataClassMapper<FROM, TO> = generated()
}