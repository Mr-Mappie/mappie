package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.util.isSubtypeOfClass
import org.jetbrains.kotlin.name.Name.identifier
import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.referenceEnumMappieClass
import tech.mappie.ir.resolving.classes.ClassResolver
import tech.mappie.ir.resolving.enums.EnumResolver
import tech.mappie.ir.resolving.enums.InheritedEnumResolver
import tech.mappie.ir.util.isSubclassOf

object MappingResolverSelector {

    context(context: MappieContext)
    fun select(definition: InternalMappieDefinition): MappingResolver =
        when {
            definition.clazz.isSubclassOf(referenceEnumMappieClass()) -> {
                if (definition.parent == null) {
                    EnumResolver(definition.source, definition.target)
                } else {
                    InheritedEnumResolver(definition)
                }
            }
            else -> {
                val declaration = definition.referenceMapFunction()
                val parameters = declaration.parameters
                    .filter { it.kind == IrParameterKind.Regular }
                    .map { it.name to it.type }

                ClassResolver(parameters, declaration.returnType)
            }
        }

    context(context: MappieContext)
    fun select(definition: GeneratedMappieDefinition): MappingResolver =
        when {
            definition.source.isSubtypeOfClass(context.pluginContext.irBuiltIns.enumClass) -> {
                EnumResolver(definition.source, definition.target)
            }
            else -> {
                ClassResolver(listOf(identifier("from") to definition.source), definition.target)
            }
        }
}