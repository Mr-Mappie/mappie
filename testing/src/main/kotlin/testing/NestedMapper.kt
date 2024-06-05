package testing

import io.github.mappie.api.DataClassMapper

data class Thing(val inner: Thong)

data class Thong(val description: String)

data class ThingDto(val inner: ThongDto)

data class ThongDto(val description: String)

object ThingMapper : DataClassMapper<Thing, ThingDto>() {
    override fun map(from: Thing): ThingDto = mapping {
        ThingDto::inner mappedFromProperty Thing::inner via ThongMapper
    }
}

object ThongMapper : DataClassMapper<Thong, ThongDto>() {
    override fun map(from: Thong): ThongDto = mapping()
}