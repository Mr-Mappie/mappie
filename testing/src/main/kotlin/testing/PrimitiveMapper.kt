package testing

import io.github.stefankoppier.mapping.annotations.Mapper

object IntMapper : Mapper<Int, String>() {

    override fun map(input: Int) = mapping {
        result(input.toString())
    }
}