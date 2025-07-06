package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol

class SourcesCollector(private val session: FirSession) {
    fun collect(clazz: FirClassSymbol<*>): List<MappingSource> {
        return clazz.declaredProperties(session).map { property ->
            PropertySource(property)
        }
    }
}