package testing

import io.github.mappie.annotations.Mapper

data class Person(val name: String)

data class PersonDto(val name: String, val fullname: String, val age: Int)

object PersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        PersonDto::fullname property Person::name
        PersonDto::age constant 26
    }
}

object TransformingPersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        PersonDto::fullname property Person::name transform { "$it Surname" }
        PersonDto::age constant 24
    }
}