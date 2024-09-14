package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.Name
import tech.mappie.util.getterName
import tech.mappie.util.merge

class ImplicitClassMappingSourcesCollector : BaseVisitor<Map<Name, ImplicitClassMappingSource>, IrValueParameter>() {

    override fun visitValueParameter(declaration: IrValueParameter, data: IrValueParameter): Map<Name, ImplicitClassMappingSource> {
        val clazz = declaration.type.getClass()!!
        val properties = clazz.properties.map { it.accept(data) }.merge()
        val methods = clazz.declarations.filterIsInstance<IrSimpleFunction>().map { it.accept(data) }.merge()
        return properties + methods
    }

    override fun visitFunction(declaration: IrFunction, data: IrValueParameter): Map<Name, ImplicitClassMappingSource> =
        if (declaration.isJavaLikeGetter()) {
            val name = getterName(declaration.name.asString().removePrefix("get").replaceFirstChar { it.lowercaseChar() })
            mapOf(name to FunctionMappingSource(declaration, data))
        } else {
            emptyMap()
        }

    override fun visitProperty(declaration: IrProperty, data: IrValueParameter): Map<Name, ImplicitClassMappingSource> =
        declaration.getter?.let { mapOf(declaration.name to ImplicitPropertyMappingSource(declaration, data, null)) } ?: emptyMap()

    private fun IrFunction.isJavaLikeGetter(): Boolean =
        name.asString().startsWith("get") && symbol.owner.valueParameters.isEmpty()
}