package testing

import org.mappie.api.Mappie

object IdentityMapper : Mappie<Int, Int>() {
    override fun map(from: Int): Int = mapping()
}