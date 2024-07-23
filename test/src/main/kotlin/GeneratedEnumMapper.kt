import tech.mappie.api.ObjectMappie

data class NestedGeneratedInputObject(val enum: InputEnum)

data class NestedGeneratedOutputObject(val enum: OutputEnum)

object NestedGeneratedInputToOutputMapper : ObjectMappie<NestedGeneratedInputObject, NestedGeneratedOutputObject>()