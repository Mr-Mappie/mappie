package testing

import tech.mappie.api.Mappie

object IntIdentityMapper : Mappie<Int, Int>() {
    override fun map(from: Int): Int = mapping()
}