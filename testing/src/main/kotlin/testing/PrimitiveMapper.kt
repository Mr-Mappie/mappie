package testing

import io.github.mappie.api.ObjectMapper

object IntMapper : ObjectMapper<Int, String>() {

    override fun map(from: Int) = mapping {
        result(from.toString())
    }
}

object StringMapper : ObjectMapper<String, Int>() {

    override fun map(from: String) = mapping {
        result(from.toInt())
    }
}
