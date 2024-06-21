package testing

import tech.mappie.api.ObjectMappie

object ExpressionMapper : ObjectMappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto = mapping {
        PersonDto::age fromConstant 10
        PersonDto::description fromExpression { it::class.simpleName!! }
    }
}