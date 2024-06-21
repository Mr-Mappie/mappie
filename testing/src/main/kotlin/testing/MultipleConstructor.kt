package testing

import tech.mappie.api.ObjectMappie

data class MultipleConstructors(val string: String)

data class MultipleConstructorsDto constructor(val string: String, val int: Int) {
    constructor(string: String) : this(string, 1)
}

object MultipleConstructorsWithoutIntMapper : ObjectMappie<MultipleConstructors, MultipleConstructorsDto>() {
    override fun map(from: MultipleConstructors): MultipleConstructorsDto = mapping()
}

object MultipleConstructorsWitIntMapper : ObjectMappie<MultipleConstructors, MultipleConstructorsDto>() {
    override fun map(from: MultipleConstructors): MultipleConstructorsDto = mapping {
        MultipleConstructorsDto::int fromConstant 2
    }
}