package tech.mappie.api

import kotlin.reflect.KClass

internal actual fun createArrayInstance(componentClass: KClass<*>?, size: Int): Any {
    TODO("Mapping Array is not supported on this platform.")
}

internal actual fun setArrayElement(array: Any, index: Int, value: Any?) {
    TODO("Mapping Array is not supported on this platform.")
}

internal actual fun inferTargetKClass(mapper: ObjectMappie<*, *>): KClass<*>? {
    TODO("Mapping Array is not supported on this platform.")
}
