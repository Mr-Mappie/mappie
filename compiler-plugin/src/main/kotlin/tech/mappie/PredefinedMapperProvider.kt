package tech.mappie

import tech.mappie.api.PredefinedMapperProvider
import java.util.ServiceLoader

fun PredefinedMapperProvider.Companion.all(): Sequence<PredefinedMapperProvider> {
    val loader = ServiceLoader.load(PredefinedMapperProvider::class.java)
    return loader.iterator().asSequence()
}
