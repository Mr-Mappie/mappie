package testing

import io.github.mappie.api.ObjectMapper

data class MultipleConstructors(val string: String)

data class MultipleConstructorsDto constructor(val string: String, val int: Int) {
    constructor(string: String) : this(string, 1)
}

object MultipleConstructorsWithoutIntMapper : ObjectMapper<MultipleConstructors, MultipleConstructorsDto>() {
    override fun map(from: MultipleConstructors): MultipleConstructorsDto = mapping()
}

object MultipleConstructorsWitIntMapper : ObjectMapper<MultipleConstructors, MultipleConstructorsDto>() {
    override fun map(from: MultipleConstructors): MultipleConstructorsDto = mapping {
        MultipleConstructorsDto::int mappedFromConstant 2
    }
}