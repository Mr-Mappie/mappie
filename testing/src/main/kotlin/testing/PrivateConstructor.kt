package testing

import tech.mappie.api.ObjectMappie

data class PrivateConstructor(val string: String)

data class PrivateConstructorDto constructor(val string: String, val int: Int) {
    private constructor(string: String) : this(string, 1)
}

object PrivateConstructorMapper : ObjectMappie<PrivateConstructor, PrivateConstructorDto>() {
    override fun map(from: PrivateConstructor): PrivateConstructorDto = mapping {
        PrivateConstructorDto::int fromValue 1
    }
}