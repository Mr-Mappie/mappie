package testing

import io.github.mappie.api.Mapper

object IdentityMapper : Mapper<Int, Int>() {
    override fun map(from: Int): Int = mapping()
}