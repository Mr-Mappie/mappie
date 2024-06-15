package testing

import io.github.mappie.api.DataClassMapper

data class MultipleConstructors(val string: String)

data class MultipleConstructorsDto constructor(val string: String, val int: Int) {
    private constructor(string: String) : this(string, 1)
}

object MultipleConstructorsMapper : DataClassMapper<MultipleConstructors, MultipleConstructorsDto>() {
    override fun map(from: MultipleConstructors): MultipleConstructorsDto = mapping {
        MultipleConstructorsDto::int mappedFromConstant 1
    }
}