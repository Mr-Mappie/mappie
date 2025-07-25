package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.isSubtypeOfClass
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.classes.ClassResolver
import tech.mappie.ir.resolving.enums.EnumResolver
import tech.mappie.ir.util.isSubclassOf
import tech.mappie.referenceEnumMappieClass

fun interface MappingResolver {

    fun resolve(function: IrFunction?): List<MappingRequest>

    companion object {
        fun of(source: IrType, target:IrType, context: ResolverContext) =
            when {
                source.isSubtypeOfClass(context.pluginContext.irBuiltIns.enumClass) -> {
                    EnumResolver(context, source, target)
                }
                else -> {
                    ClassResolver(context, listOf(Name.identifier("from") to source), target)
                }
            }

        fun of(declaration: IrFunction, context: ResolverContext) =
            when {
                declaration.parentAsClass.isSubclassOf(context.referenceEnumMappieClass()) -> {
                    EnumResolver(context, declaration.parameters[1].type, declaration.returnType)
                }
                else -> {
                    val parameters = declaration.parameters
                        .filter { it.kind == IrParameterKind.Regular }
                        .map { it.name to it.type }

                    ClassResolver(context, parameters, declaration.returnType)
                }
            }
    }
}
