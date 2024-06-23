package testing

import tech.mappie.api.ObjectMappie

class ConstructorParameterWhichIsNotAField(
    val parameter: String
)

class ConstructorParameterWhichIsNotAFieldDto(
    value: String
) {
    val property = value

    override fun equals(other: Any?): Boolean {
        if (other is ConstructorParameterWhichIsNotAFieldDto) {
            return property == other.property
        }
        return false
    }

    override fun hashCode(): Int =
        property.hashCode()
}

object ConstructorParameterWhichIsNotAFieldMapper : ObjectMappie<ConstructorParameterWhichIsNotAField, ConstructorParameterWhichIsNotAFieldDto>() {
    override fun map(from: ConstructorParameterWhichIsNotAField): ConstructorParameterWhichIsNotAFieldDto = mapping {
        parameter(PARAMETER_NAME) fromProperty ConstructorParameterWhichIsNotAField::parameter
    }

    private const val PARAMETER_NAME = "v" + "alue"
}
