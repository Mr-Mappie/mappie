package tech.mappie.util

fun <T> Collection<List<T>>.filterSingle(): List<T> =
    filter { it.size == 1 }.map { it.single() }
