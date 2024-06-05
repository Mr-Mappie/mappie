package testing

import io.github.mappie.api.DataClassMapper

object IntMapper : DataClassMapper<Int, String>() {

    override fun map(from: Int) = mapping {
        result(from.toString())
    }
}

object StringMapper : DataClassMapper<String, Int>() {

    override fun map(from: String) = mapping {
        result(from.toInt())
    }
}
