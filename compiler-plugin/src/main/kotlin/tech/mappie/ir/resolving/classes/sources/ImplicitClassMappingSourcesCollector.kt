package tech.mappie.ir.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.isFromJava
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import tech.mappie.ir.util.BaseVisitor
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.ir.util.isSubclassOf
import tech.mappie.util.merge
import tech.mappie.ir.util.substituteTypeVariable

class ImplicitClassMappingSourcesCollector(private val context: MappieContext)
    : BaseVisitor<Map<Name, ImplicitClassMappingSource>, Pair<Name, IrType>>() {

    override fun visitClass(declaration: IrClass, data: Pair<Name, IrType>): Map<Name, ImplicitClassMappingSource> {
        val properties = declaration.properties.map { it.accept(data) }.merge()
        val methods = declaration.declarations.filterIsInstance<IrSimpleFunction>().map { it.accept(data) }.merge()
        return properties + methods
    }

    override fun visitFunction(declaration: IrFunction, data: Pair<Name, IrType>): Map<Name, ImplicitClassMappingSource> =
        if (declaration.isJavaGetter()) {
            val name = Name.identifier(declaration.name.asString().removePrefix("get").replaceFirstChar { it.lowercaseChar() })
            val type = declaration.returnType.substituteTypeVariable(declaration.parentAsClass, (data.second as IrSimpleType).arguments)
            mapOf(name to FunctionMappingSource(declaration, type, data.first, data.second, null))
        } else {
            emptyMap()
        }

    override fun visitProperty(declaration: IrProperty, data: Pair<Name, IrType>): Map<Name, ImplicitClassMappingSource> =
        declaration.getter?.let {
            val propertyType = it.returnType.substituteTypeVariable(declaration.parentAsClass, (data.second as IrSimpleType).arguments)
            mapOf(declaration.name to ImplicitPropertyMappingSource(declaration, propertyType, data.first, data.second, null))
        } ?: emptyMap()

    private fun IrFunction.isJavaGetter(): Boolean {
        if (isFromJava()) {
            if (parentAsClass.isSubclassOf(context.pluginContext.referenceClass(ClassId(FqName("java.lang"), Name.identifier("Record")))!!)) {
                return parentAsClass.primaryConstructor!!.parameters.any { it.name == name }
            }
            return name.asString().startsWith("get") && symbol.owner.parameters.none { it.kind == IrParameterKind.Regular }
        }
        return false
    }
}