package testing

import io.github.stefankoppier.mapping.annotations.Mapper

data class Person(val name: String)

data class PersonDto(val namee: String)

object PersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        Person::name mappedTo PersonDto::namee
    }
}