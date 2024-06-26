package testing

import tech.mappie.api.ObjectMappie

data class LongInput(val value: Long)

data class IntOutput(val value: Int)

object IntToLongMapper : ObjectMappie<LongInput, IntOutput>()