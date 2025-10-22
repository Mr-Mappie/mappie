package tech.mappie.state

import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.name.ClassId

data class MappieReference(val id: ClassId) {

    companion object {
        fun of(clazz: FirClass) =
            MappieReference(clazz.symbol.classId)
    }
}
