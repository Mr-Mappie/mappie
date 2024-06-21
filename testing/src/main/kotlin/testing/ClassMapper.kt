package testing

import tech.mappie.api.ObjectMappie

class Class(
    private val field: String,
    argument: Int,
)

class ClassDto(
    field: String,
    val argument: Int
) {
    override fun equals(other: Any?): Boolean {
        if (other != null && other is ClassDto) {
            return argument == other.argument
        }
        return false
    }

    override fun hashCode() =
        argument.hashCode()
}

object ClassMapper : ObjectMappie<Class, ClassDto>() {
    override fun map(from: Class) = mapping {
        ClassDto::argument fromConstant 1
    }
}