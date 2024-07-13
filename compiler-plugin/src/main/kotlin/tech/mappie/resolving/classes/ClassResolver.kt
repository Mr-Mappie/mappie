package tech.mappie.resolving.classes

import tech.mappie.resolving.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.resolving.classes.sources.MappieSourcesCollector
import tech.mappie.resolving.classes.targets.MappieTargetsCollector

class ClassResolver(private val declaration: IrFunction, private val symbols: List<MappieDefinition>) {

    private val sourceParameters = declaration.valueParameters

    fun resolve(): List<ConstructorCallMapping> {
        return declaration.accept(ConstructorsCollector(declaration.fileEntry), Unit).map { constructor ->
            ObjectMappingsConstructor(
                symbols,
                constructor,
                sourceParameters.flatMap { it.accept(MappieSourcesCollector(declaration.fileEntry), it) },
                MappieTargetsCollector(constructor).all()
            ).also {
                declaration.body?.accept(ObjectMappingBodyCollector(declaration.fileEntry), it)
            }.construct()
        }
    }
}