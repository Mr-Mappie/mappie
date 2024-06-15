package testing

import io.github.mappie.api.DataClassMapper

data class DefaultValue(val string: String)

data class DefaultValueDto(val string: String, val int: Int = 10)

object DefaultValueMapper : DataClassMapper<DefaultValue, DefaultValueDto>() {
    override fun map(from: DefaultValue): DefaultValueDto = mapping()
}
