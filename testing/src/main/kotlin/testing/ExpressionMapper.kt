package testing

import io.github.mappie.api.ObjectMapper

object ExpressionMapper : ObjectMapper<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::age mappedFromConstant 10
        PersonDto::description mappedFromExpression { it::class.simpleName!! }
    }
}