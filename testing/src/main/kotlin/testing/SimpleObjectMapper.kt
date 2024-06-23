package testing

import tech.mappie.api.ObjectMappie

class StringValue(val value: String)

data class StringValueDto(val value: String)

object StringValueMapper : ObjectMappie<StringValue, StringValueDto>()