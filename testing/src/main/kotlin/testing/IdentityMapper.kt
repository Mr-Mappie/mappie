package testing

import tech.mappie.api.Mappie

object IdentityMapper : Mappie<Int, Int>() {
    override fun map(from: Int): Int = mapping()
}