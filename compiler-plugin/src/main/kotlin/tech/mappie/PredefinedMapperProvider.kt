package tech.mappie

import tech.mappie.api.PredefinedMappieProvider
import java.util.ServiceLoader

fun PredefinedMappieProvider.Companion.all(): Sequence<PredefinedMappieProvider> {
    val loader = ServiceLoader.load(PredefinedMappieProvider::class.java)
    return loader.iterator().asSequence()
}
