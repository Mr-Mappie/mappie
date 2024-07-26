import tech.mappie.api.ObjectMappie

data class NestedGeneratedInputObject(val enum: InputEnumTwo)

enum class InputEnumTwo {
    A, B, C, D;
}

data class NestedGeneratedOutputObject(val enum: OutputEnumTwo)

enum class OutputEnumTwo {
    A, B, C, D, E;
}

object NestedGeneratedInputToOutputMapper : ObjectMappie<NestedGeneratedInputObject, NestedGeneratedOutputObject>()