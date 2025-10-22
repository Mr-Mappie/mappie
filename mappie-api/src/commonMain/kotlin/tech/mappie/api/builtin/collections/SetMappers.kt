package tech.mappie.api.builtin.collections

import tech.mappie.api.Mappie1
import tech.mappie.api.MappieResolved
import tech.mappie.api.ObjectMappie
import tech.mappie.api.generated

public class IterableToSetMapper<FROM, TO>(@MappieResolved private val inner: Mappie1<FROM, TO> = generated()) : ObjectMappie<Iterable<FROM>, Set<TO>>() {

    override fun map(from: Iterable<FROM>): Set<TO> =
        from.map( inner::map).toSet()
}