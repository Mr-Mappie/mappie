import tech.mappie.api.ObjectMappie

data class InputObject(
    val name: String,
    val age: Int,
    val nested: NestedInput,
)

data class NestedInput(val boolean: BooleanEnum) {
    enum class BooleanEnum { TRUE, FALSE }
}

data class OutputObject(
    val name: String,
    val age: Int,
    val boolean: Boolean,
)

object ObjectMapperWithoutVia : ObjectMappie<InputObject, OutputObject>() {
    override fun map(from: InputObject) = mapping {
        to::boolean fromProperty from.nested::boolean
    }
}

object ObjectMapper : ObjectMappie<InputObject, OutputObject>() {
    override fun map(from: InputObject) = mapping {
        to::boolean fromProperty from.nested::boolean via BooleanEnumToBooleanMapper
    }
}

object BooleanEnumToBooleanMapper : ObjectMappie<NestedInput.BooleanEnum, Boolean>() {
    override fun map(from: NestedInput.BooleanEnum) = when (from) {
        NestedInput.BooleanEnum.TRUE -> !false
        NestedInput.BooleanEnum.FALSE -> !true
    }
}
