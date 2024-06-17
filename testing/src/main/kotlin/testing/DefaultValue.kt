package testing

import io.github.mappie.api.ObjectMapper

data class DefaultValue(val string: String)

data class DefaultValueDto(val string: String, val int: Int = 10)

object DefaultValueMapper : ObjectMapper<DefaultValue, DefaultValueDto>() {
    override fun map(from: DefaultValue): DefaultValueDto = mapping()
}
