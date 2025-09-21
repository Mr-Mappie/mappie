package tech.mappie.api

import kotlin.reflect.KClass

internal expect fun createArrayInstance(componentClass: KClass<*>?, size: Int): Any
internal expect fun setArrayElement(array: Any, index: Int, value: Any?)
internal expect fun inferTargetKClass(mapper: ObjectMappie<*, *>): KClass<*>?
