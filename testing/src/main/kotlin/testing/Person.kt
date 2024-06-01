package testing

import io.github.stefankoppier.mapping.annotations.Mapper

data class Person(val name: String)

data class PersonDto(val namee: String, val name: String, val age: Int)

object PersonMapper : Mapper<Person, PersonDto>() {

    override fun map(from: Person): PersonDto = mapping {
        Person::name mappedTo PersonDto::namee
        1 constant PersonDto::age
    }
}