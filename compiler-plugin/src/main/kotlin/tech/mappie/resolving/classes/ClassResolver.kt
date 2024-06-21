package tech.mappie.resolving.classes

import tech.mappie.resolving.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.isClass

class ClassResolver(private val declaration: IrFunction) {

    private val sourceParameter = declaration.valueParameters.first()

    init {
        require(declaration.returnType.getClass()!!.isClass)
    }

    fun resolve(): List<ConstructorCallMapping> {
        val constructor = ObjectMappingsConstructor.of(declaration.returnType, sourceParameter)
            .apply { getters.addAll(sourceParameter.accept(GettersCollector(), Unit)) }

        declaration.body?.accept(ObjectMappingBodyCollector(declaration.fileEntry, sourceParameter.symbol), constructor)

        return declaration.accept(ConstructorsCollector(), Unit).map {
            ObjectMappingsConstructor.of(constructor).apply {
                this.constructor = it
            }.construct()
        }
    }
}