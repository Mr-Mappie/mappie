@file:Suppress("unused")

package io.github.mappie.api

import kotlin.reflect.KProperty1

abstract class CollectionMapper<FROM, TO> : Mapper<List<FROM>, List<TO>>() {

    abstract infix fun filteredBy(predicate: (FROM) -> Boolean): CollectionMapper<FROM, TO>
}

abstract class ObjectMapper<FROM, TO> : Mapper<FROM, TO>() {

    val forList: CollectionMapper<FROM, TO> get() =
        error("The mapper forList should only be used in the context of 'via'. Use mapList instead.")

    protected infix fun <TO_TYPE, FROM_TYPE> KProperty1<TO, TO_TYPE>.mappedFromProperty(source: KProperty1<FROM, FROM_TYPE>): TransformableValue<FROM_TYPE, TO_TYPE> =
        generated()

    protected infix fun <TO_TYPE> KProperty1<TO, TO_TYPE>.mappedFromConstant(value: TO_TYPE): ObjectMapper<FROM, TO_TYPE> =
        generated()

    protected infix fun <TO_TYPE> KProperty1<TO, TO_TYPE>.mappedFromExpression(function: (FROM) -> TO_TYPE): ObjectMapper<FROM, TO_TYPE> =
        generated()

    protected fun parameter(name: String): KProperty1<TO, *> =
        generated()

    protected fun result(source: TO): ObjectMapper<FROM, TO> = generated()
}