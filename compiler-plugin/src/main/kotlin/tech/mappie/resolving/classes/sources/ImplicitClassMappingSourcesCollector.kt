package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.*
import tech.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.Name
import tech.mappie.util.getterName
import tech.mappie.util.merge

class ImplicitClassMappingSourcesCollector : BaseVisitor<Map<Name, ImplicitClassMappingSource>, Name>() {

    override fun visitClass(declaration: IrClass, data: Name): Map<Name, ImplicitClassMappingSource> {
        val properties = declaration.properties.map { it.accept(data) }.merge()
        val methods = declaration.declarations.filterIsInstance<IrSimpleFunction>().map { it.accept(data) }.merge()
        return properties + methods
    }

    override fun visitFunction(declaration: IrFunction, data: Name): Map<Name, ImplicitClassMappingSource> =
        if (declaration.isJavaLikeGetter()) {
            val name = getterName(declaration.name.asString().removePrefix("get").replaceFirstChar { it.lowercaseChar() })
            mapOf(name to FunctionMappingSource(declaration, data))
        } else {
            emptyMap()
        }

    override fun visitProperty(declaration: IrProperty, data: Name): Map<Name, ImplicitClassMappingSource> =
        declaration.getter?.let { mapOf(declaration.name to ImplicitPropertyMappingSource(declaration, data, null)) } ?: emptyMap()

    private fun IrFunction.isJavaLikeGetter(): Boolean =
        name.asString().startsWith("get") && symbol.owner.valueParameters.isEmpty()
}