@file:Suppress("UNCHECKED_CAST", "unused")

package tech.mappie.testing

import tech.mappie.api.*
import java.net.URLClassLoader
import kotlin.reflect.KClass

fun <FROM : Enum<*>, TO> URLClassLoader.loadEnumMappieClass(name: String) =
    loadClass(name).kotlin as KClass<EnumMappie<FROM, TO>>

fun <FROM, TO> URLClassLoader.loadObjectMappieClass(name: String) =
    loadClass(name).kotlin as KClass<ObjectMappie<FROM, TO>>

fun <FROM1, FROM2, TO> URLClassLoader.loadObjectMappie2Class(name: String) =
    loadClass(name).kotlin as KClass<ObjectMappie2<FROM1, FROM2, TO>>

fun <FROM1, FROM2, FROM3, TO> URLClassLoader.loadObjectMappie3Class(name: String) =
    loadClass(name).kotlin as KClass<ObjectMappie3<FROM1, FROM2, FROM3, TO>>

fun <FROM1, FROM2, FROM3, FROM4, TO> URLClassLoader.loadObjectMappie4Class(name: String) =
    loadClass(name).kotlin as KClass<ObjectMappie4<FROM1, FROM2, FROM3, FROM4, TO>>

fun <FROM1, FROM2, FROM3, FROM4, FROM5, TO> URLClassLoader.loadObjectMappie5Class(name: String) =
    loadClass(name).kotlin as KClass<ObjectMappie5<FROM1, FROM2, FROM3, FROM4, FROM5, TO>>