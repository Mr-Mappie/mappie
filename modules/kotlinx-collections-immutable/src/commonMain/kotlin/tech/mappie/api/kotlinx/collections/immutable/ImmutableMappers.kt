package tech.mappie.api.kotlinx.collections.immutable

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import tech.mappie.api.Mappie1
import tech.mappie.api.ObjectMappie

public class IterableToImmutableListMapper<FROM, TO>(private val inner: Mappie1<FROM, TO>)
    : ObjectMappie<Iterable<FROM>, ImmutableList<TO>>() {

    override fun map(from: Iterable<FROM>): ImmutableList<TO> =
        from.mapTo(mutableListOf(), inner::map).toImmutableList()
}

public class IterableToImmutableSetMapper<FROM, TO>(private val inner: Mappie1<FROM, TO>)
    : ObjectMappie<Iterable<FROM>, ImmutableSet<TO>>() {
    
    override fun map(from: Iterable<FROM>): ImmutableSet<TO> =
        from.mapTo(mutableSetOf(), inner::map).toImmutableSet()
}
