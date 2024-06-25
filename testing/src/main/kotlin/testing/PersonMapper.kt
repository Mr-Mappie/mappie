package testing

import tech.mappie.api.ObjectMappie
import tech.mappie.api.Mappie

data class Person(val name: String)

data class PersonDto(val name: String, val description: String, val age: Int)

object PersonMapper : ObjectMappie<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromProperty Person::name
        PersonDto::age fromValue 26
    }
}

object ConstructorCallPersonMapper : Mappie<Person, PersonDto>() {
    override fun map(from: Person): PersonDto {
        return from.name.let { name ->
            PersonDto(name, "description", 10)
        }
    }
}

object TransformingPersonMapper : ObjectMappie<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description fromProperty Person::name transform { "$it Surname" }
        PersonDto::age fromValue 24
    }
}