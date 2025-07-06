package tech.mappie.fir.resolving

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.constructors
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.types.type
import tech.mappie.fir.resolving.classes.MappingSource
import tech.mappie.fir.resolving.classes.MappingTarget
import tech.mappie.fir.resolving.classes.SourcesCollector
import tech.mappie.fir.resolving.classes.TargetsCollector
import tech.mappie.fir.util.isMappableFrom

class MappingResolver(session: FirSession) {

    val collector = MappingOptionsCollector(session)

    fun resolve(clazz: FirClassSymbol<*>): Mapping? {
        val options = collector.collect(clazz)
        return options.first() // TODO: assuming single constructor and correctness
    }
}

data class Mapping(
    val constructor: FirConstructorSymbol,
    val mappings: Map<MappingTarget, MappingSource?>,
)

class MappingOptionsCollector(private val session: FirSession) {

    val targetsCollector = TargetsCollector()

    val sourcesCollector = SourcesCollector(session)

    @OptIn(SymbolInternals::class)
    fun collect(clazz: FirClassSymbol<*>): List<Mapping> {
        val superType = clazz.resolvedSuperTypes.first() // TODO: only works for ObjectMappie
        val (source, target) = superType.typeArguments.take(2).map { it.type!! }

        return target.toClassSymbol(session)!!.constructors(session).map { constructor ->
            val targets = targetsCollector.collect(constructor.fir)
            val sources = sourcesCollector.collect(source.toClassSymbol(session)!!)

            context(session) {
                val mappings = targets.associateWith { target ->
                    sources.first { source -> source.type.isMappableFrom(target.type) }
                }
                Mapping(constructor, mappings)
            }
        }
    }
}




