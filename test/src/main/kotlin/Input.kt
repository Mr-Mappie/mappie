import tech.mappie.api.ObjectMappie

class Input(
    val name: String,
    val age: Int,
    val nested: NestedInput,
)

object InputToOutputMapper : ObjectMappie<Input, Output>() {

    override fun map(from: Input) = mapping {
        to::boolean fromProperty from.nested::boolean via BooleanEnumToBooleanMapper
    }

    private object BooleanEnumToBooleanMapper : ObjectMappie<NestedInput.BooleanEnum, Boolean>() {
        override fun map(from: NestedInput.BooleanEnum) =
            when (from) {
                NestedInput.BooleanEnum.TRUE -> !false
                NestedInput.BooleanEnum.FALSE -> !true
            }
    }
}