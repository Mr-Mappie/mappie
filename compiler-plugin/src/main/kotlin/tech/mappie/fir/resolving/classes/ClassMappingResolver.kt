package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.constructors
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.type
import tech.mappie.fir.resolving.ClassMapping
import tech.mappie.fir.util.isMappableFrom

class ClassMappingResolver(session: FirSession) {

    private val collector = MappingOptionsCollector(session)

    fun resolve(clazz: FirClassSymbol<*>): ClassMapping? {
        val options = collector.collect(clazz)
        return options.first() // TODO: assuming single constructor and correctness
    }
}

private class MappingOptionsCollector(private val session: FirSession) {

    val targetsCollector = TargetsCollector()

    val sourcesCollector = ImplicitSourcesCollector(session)

    @OptIn(SymbolInternals::class)
    fun collect(clazz: FirClassSymbol<*>): List<ClassMapping> {
        val superType = clazz.resolvedSuperTypes.first() // TODO: assumes direct inheritance
        val (source, target) = superType.typeArguments.take(2).map { it.type!! } // TODO: only works for ObjectMappie

        return target.toClassSymbol(session)!!.constructors(session).map { constructor ->
            val targets = targetsCollector.collect(constructor.fir)
            val sources = sourcesCollector.collect(source.toClassSymbol(session)!!)
            val explicit = clazz.fir.accept(ExplicitClassMappingCollector(session), Unit)

            context(session) {
                val mappings = targets.associateWith { target ->
                    explicit(target, explicit) ?: implicit(target, sources)
                }
                ClassMapping(constructor, mappings)
            }
        }
    }

    context (session: FirSession)
    private fun explicit(target: ClassMappingTarget, explicit: Map<ClassMappingTarget, ClassMappingSource>): ClassMappingSource? {
        val matching = explicit.toList().filter { it.first.name == target.name }
        // TODO: if multiple, throw an error that multiple explicit mappings are defined.
        return matching.singleOrNull()?.second
    }

    context (session: FirSession)
    private fun implicit(target: ClassMappingTarget, sources: List<ClassMappingSource>): ClassMappingSource {
        return sources.first { source -> source.type.isMappableFrom(target.type) }
    }
}
