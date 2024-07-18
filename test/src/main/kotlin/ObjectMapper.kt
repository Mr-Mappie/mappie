import tech.mappie.api.ObjectMappie

data class InputObject(
    val name: String,
    val age: Int,
    val nested: NestedInput,
)

data class NestedInput(val boolean: BooleanEnum) {
    enum class BooleanEnum { TRUE, FALSE }
}

class OutputObject(
    val name: String,
    val age: Int,
    val boolean: Boolean,
)

object ObjectMapper : ObjectMappie<InputObject, OutputObject>() {

    override fun map(from: InputObject) = mapping {
        to::boolean fromProperty from.nested::boolean via BooleanEnumToBooleanMapper
    }

    private object BooleanEnumToBooleanMapper : ObjectMappie<NestedInput.BooleanEnum, Boolean>() {
        override fun map(from: NestedInput.BooleanEnum) = when (from) {
            NestedInput.BooleanEnum.TRUE -> !false
            NestedInput.BooleanEnum.FALSE -> !true
        }
    }
}