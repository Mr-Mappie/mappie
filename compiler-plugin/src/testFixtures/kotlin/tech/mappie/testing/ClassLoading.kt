package tech.mappie.testing

import tech.mappie.api.EnumMappie
import tech.mappie.api.ObjectMappie
import java.net.URLClassLoader
import kotlin.reflect.KClass

fun <FROM : Enum<*>, TO : Enum<*>> URLClassLoader.loadEnumMappieClass(name: String) =
    loadClass(name).kotlin as KClass<EnumMappie<FROM, TO>>


fun <FROM, TO> URLClassLoader.loadObjectMappieClass(name: String) =
    loadClass(name).kotlin as KClass<ObjectMappie<FROM, TO>>