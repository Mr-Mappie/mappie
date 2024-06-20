package testing

import io.github.mappie.api.ObjectMappie

object ExpressionMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::age mappedFromConstant 10
        PersonDto::description mappedFromExpression { it::class.simpleName!! }
    }
}