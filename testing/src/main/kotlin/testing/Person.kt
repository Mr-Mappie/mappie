package testing

import io.github.stefankoppier.mapping.annotations.Mapper

data class Person(val name: String, val age: Long)

data class PersonDto(val name: String)

object PersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping()
}