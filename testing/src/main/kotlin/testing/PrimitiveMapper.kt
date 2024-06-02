package testing

import io.github.stefankoppier.mapping.annotations.Mapper

object IntMapper : Mapper<Int, String>() {

    override fun map(from: Int) = mapping {
        result(from.toString())
    }
}

object StringMapper : Mapper<String, Int>() {

    override fun map(from: String) = mapping {
        result(from.toInt())
    }
}