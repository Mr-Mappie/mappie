package testing

import io.github.mappie.api.ObjectMapper

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

object ConstructorParameterWhichIsNotAFieldMapper : ObjectMapper<ConstructorParameterWhichIsNotAField, ConstructorParameterWhichIsNotAFieldDto>() {
    override fun map(from: ConstructorParameterWhichIsNotAField): ConstructorParameterWhichIsNotAFieldDto = mapping {
        parameter("value") mappedFromProperty ConstructorParameterWhichIsNotAField::parameter
    }
}
