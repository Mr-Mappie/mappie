package testing

import io.github.mappie.annotations.Mapper

data class Person(val name: String)

data class PersonDto(val name: String, val description: String, val age: Int)

object PersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description property Person::name
        PersonDto::age constant 26
    }
}

object ConstructorCallPersonMapper : Mapper<Person, PersonDto>() {
    override fun map(from: Person): PersonDto {
        return from.name.let { name ->
            PersonDto(name, "description", 10)
        }
    }
}

object TransformingPersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        PersonDto::description property Person::name transform { "$it Surname" }
        PersonDto::age constant 24
    }
}