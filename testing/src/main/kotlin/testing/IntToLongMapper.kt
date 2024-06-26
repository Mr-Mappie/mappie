package testing

import tech.mappie.api.ObjectMappie

data class IntInput(val value: Int)

data class LongOutput(val value: Long)

object IntToLongMapper : ObjectMappie<IntInput, LongOutput>()