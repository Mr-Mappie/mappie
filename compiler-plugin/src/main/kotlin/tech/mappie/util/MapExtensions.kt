package tech.mappie.util

fun <K, V> List<Map<K, V>>.merge(): Map<K, V> = fold(emptyMap(), Map<K, V>::plus)

fun <K, V> Sequence<Map<K, V>>.merge(): Map<K, V> = fold(emptyMap(), Map<K, V>::plus)