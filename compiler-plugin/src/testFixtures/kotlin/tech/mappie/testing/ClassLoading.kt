package tech.mappie.testing

import tech.mappie.api.EnumMappie
import java.net.URLClassLoader
import kotlin.reflect.KClass

fun <FROM : Enum<*>, TO : Enum<*>> URLClassLoader.loadEnumMappieClass(name: String) =
    loadClass(name).kotlin as KClass<EnumMappie<FROM, TO>>