package testing

import io.github.mappie.api.DataClassMapper

data class PrivateConstructor(val string: String)

data class PrivateConstructorDto constructor(val string: String, val int: Int) {
    private constructor(string: String) : this(string, 1)
}

object PrivateConstructorMapper : DataClassMapper<PrivateConstructor, PrivateConstructorDto>() {
    override fun map(from: PrivateConstructor): PrivateConstructorDto = mapping {
        PrivateConstructorDto::int mappedFromConstant 1
    }
}