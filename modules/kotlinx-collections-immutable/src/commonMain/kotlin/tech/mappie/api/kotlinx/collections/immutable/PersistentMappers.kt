package tech.mappie.api.kotlinx.collections.immutable

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import tech.mappie.api.Mappie1
import tech.mappie.api.ObjectMappie

public class IterableToPersistentListMapper<FROM, TO>(private val inner: Mappie1<FROM, TO>) :
    ObjectMappie<Iterable<FROM>, PersistentList<TO>>() {

    override fun map(from: Iterable<FROM>): PersistentList<TO> =
        from.mapTo(mutableListOf(), inner::map).toPersistentList()
}

public class IterableToPersistentSetMapper<FROM, TO>(private val inner: Mappie1<FROM, TO>) :
    ObjectMappie<Iterable<FROM>, PersistentSet<TO>>() {

    override fun map(from: Iterable<FROM>): PersistentSet<TO> =
        from.mapTo(mutableSetOf(), inner::map).toPersistentSet()
}
