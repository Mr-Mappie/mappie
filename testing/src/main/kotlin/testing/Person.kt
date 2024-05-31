package testing

import io.github.stefankoppier.mapping.annotations.Mapper

data class Person(val name: String)

data class PersonDto(val name: String)

fun map(from: Person): PersonDto =
    PersonDto(from.name)

class PersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        Person::name mappedTo PersonDto::name
    }
}