package tech.mappie.resolving.classes

import tech.mappie.resolving.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.isClass

class ClassResolver(private val declaration: IrFunction, private val symbols: List<MappieDefinition>) {

    private val sourceParameters = declaration.valueParameters

    init {
        require(declaration.returnType.getClass()!!.isClass)
    }

    fun resolve(): List<ConstructorCallMapping> {
        val constructor = ObjectMappingsConstructor.of(declaration.returnType, sourceParameters)
            .apply { getters.addAll(sourceParameters.flatMap { it.accept(GettersCollector(declaration.fileEntry), it) }) }

        declaration.body?.accept(ObjectMappingBodyCollector(declaration.fileEntry), constructor)

        return declaration.accept(ConstructorsCollector(declaration.fileEntry), Unit).map {
            ObjectMappingsConstructor.of(constructor).apply {
                this.constructor = it
                this.symbols = this@ClassResolver.symbols
            }.construct()
        }
    }
}