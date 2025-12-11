package tech.mappie.api.kotlinx.collections.immutable

import tech.mappie.api.PredefinedMappieProvider

public class KotlinxCollectionsImmutableMappieProvider : PredefinedMappieProvider {
    override val common: List<String> = listOf(
        "tech/mappie/api/kotlinx/collections/immutable/IterableToImmutableListMapper",
        "tech/mappie/api/kotlinx/collections/immutable/IterableToImmutableSetMapper",
    )
    
    public override val jvm: List<String> = emptyList()
}