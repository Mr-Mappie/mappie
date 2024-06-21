package testing

import org.mappie.api.ObjectMappie

object IntMapper : ObjectMappie<Int, String>() {

    override fun map(from: Int) = from.toString()
}

object StringMapper : ObjectMappie<String, Int>() {

    override fun map(from: String) = from.toInt()
}
