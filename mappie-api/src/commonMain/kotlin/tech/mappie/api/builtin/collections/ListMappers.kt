package tech.mappie.api.builtin.collections

import tech.mappie.api.Mappie1
import tech.mappie.api.MappieResolved
import tech.mappie.api.ObjectMappie

public class IterableToListMapper<FROM, TO>(@MappieResolved private val inner: Mappie1<FROM, TO>)
    : ObjectMappie<Iterable<FROM>, List<TO>>() {

    override fun map(from: Iterable<FROM>): List<TO> =
        from.map(inner::map).toList()
}
