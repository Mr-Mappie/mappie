package testing

import tech.mappie.api.ObjectMappie

data class DefaultValue(val string: String)

data class DefaultValueDto(val string: String, val int: Int = 10)

object DefaultValueMapper : ObjectMappie<DefaultValue, DefaultValueDto>() {
    override fun map(from: DefaultValue): DefaultValueDto = mapping()
}
