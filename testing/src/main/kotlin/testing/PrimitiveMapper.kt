package testing

import io.github.mappie.annotations.Mapper

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