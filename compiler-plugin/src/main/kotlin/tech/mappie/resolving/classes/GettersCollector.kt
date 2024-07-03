package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.properties

class GettersCollector : BaseVisitor<List<MappieGetter>, Unit>() {

    override fun visitValueParameter(declaration: IrValueParameter, data: Unit): List<MappieGetter> {
        return declaration.type.getClass()!!.properties.flatMap { it.accept(data) }.toList() +
            declaration.type.getClass()!!.declarations.filterIsInstance<IrSimpleFunction>().flatMap { it.accept(data) }
    }

    override fun visitFunction(declaration: IrFunction, data: Unit): List<MappieGetter> {
        if (declaration.name.asString().startsWith("get") && declaration.symbol.owner.valueParameters.isEmpty()) {
            return listOf(MappieFunctionGetter(declaration))
        }
        return emptyList()
    }

    override fun visitProperty(declaration: IrProperty, data: Unit): List<MappieGetter> {
        return declaration.getter?.let { listOf(MappiePropertyGetter(it)) } ?: emptyList()
    }
}