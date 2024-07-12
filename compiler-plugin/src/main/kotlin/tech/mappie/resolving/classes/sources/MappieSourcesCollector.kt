package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.properties

class MappieSourcesCollector(file: IrFileEntry) : BaseVisitor<List<MappieSource>, IrValueParameter>(file) {

    override fun visitValueParameter(declaration: IrValueParameter, data: IrValueParameter): List<MappieSource> {
        val clazz = declaration.type.getClass()!!
        val properties = clazz.properties.flatMap { it.accept(data) }.toList()
        val methods = clazz.declarations.filterIsInstance<IrSimpleFunction>().flatMap { it.accept(data) }
        return properties + methods
    }

    override fun visitFunction(declaration: IrFunction, data: IrValueParameter): List<MappieSource> {
        if (declaration.name.asString().startsWith("get") && declaration.symbol.owner.valueParameters.isEmpty()) {
            return listOf(MappieFunctionSource(declaration, data))
        }
        return emptyList()
    }

    override fun visitProperty(declaration: IrProperty, data: IrValueParameter): List<MappieSource> {
        return declaration.getter?.let { listOf(MappiePropertySource(it, data)) } ?: emptyList()
    }
}