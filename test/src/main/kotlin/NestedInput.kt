import tech.mappie.api.ObjectMappie

data class NestedInput(
    val boolean: BooleanEnum,
) {
    enum class BooleanEnum { TRUE, FALSE }
}

class NestedInputToNestedOutputMapper : ObjectMappie<NestedInput, NestedOutput>()