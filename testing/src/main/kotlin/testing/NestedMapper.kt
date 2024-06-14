package testing

import io.github.mappie.api.DataClassMapper
import io.github.mappie.api.EnumMapper

enum class Boolean {
    TRUE,
    FALSE,
}

data class Thing(val inner: Thang, val boolean: Boolean)

data class Thang(val description: String)

enum class BooleanDto {
    TRUE,
    FALSE,
}

data class ThingDto(val inner: ThangDto, val boolean: BooleanDto)

data class ThangDto(val description: String)

object ThingMapper : DataClassMapper<Thing, ThingDto>() {
    override fun map(from: Thing): ThingDto = mapping {
        ThingDto::inner mappedFromProperty Thing::inner via ThangMapper
        ThingDto::boolean mappedFromProperty Thing::boolean via BooleanMapper()
    }
}

object ThangMapper : DataClassMapper<Thang, ThangDto>() {
    override fun map(from: Thang): ThangDto = mapping()
}

class BooleanMapper : EnumMapper<Boolean, BooleanDto>() {
    override fun map(from: Boolean): BooleanDto = mapping()
}