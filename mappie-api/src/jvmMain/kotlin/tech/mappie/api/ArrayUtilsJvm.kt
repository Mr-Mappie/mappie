package tech.mappie.api

import java.lang.reflect.Array
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

internal actual fun createArrayInstance(componentClass: KClass<*>?, size: Int): Any =
    Array.newInstance(componentClass!!.java, size)

internal actual fun setArrayElement(array: Any, index: Int, value: Any?) =
    Array.set(array, index, value)

internal actual fun inferTargetKClass(mapper: ObjectMappie<*, *>): KClass<*>? =
    (mapper.javaClass.genericSuperclass as ParameterizedType)
        .let { (it.actualTypeArguments[1] as Class<*>).kotlin }
