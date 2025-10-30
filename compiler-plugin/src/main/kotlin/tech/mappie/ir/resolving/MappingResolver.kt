package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.isSubtypeOfClass
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.classes.ClassResolver
import tech.mappie.ir.resolving.enums.EnumResolver
import tech.mappie.ir.util.isSubclassOf
import tech.mappie.ir.referenceEnumMappieClass

fun interface MappingResolver {

    context(context: MappieContext)
    fun resolve(origin: InternalMappieDefinition, function: IrFunction?): List<MappingRequest>

    companion object {
        context(context: MappieContext)
        fun of(source: IrType, target:IrType) =
            when {
                source.isSubtypeOfClass(context.pluginContext.irBuiltIns.enumClass) -> {
                    EnumResolver(source, target)
                }
                else -> {
                    ClassResolver(listOf(Name.identifier("from") to source), target)
                }
            }

        context(context: MappieContext)
        fun of(declaration: IrFunction) =
            when {
                declaration.parentAsClass.isSubclassOf(referenceEnumMappieClass()) -> {
                    EnumResolver(declaration.parameters[1].type, declaration.returnType)
                }
                else -> {
                    val parameters = declaration.parameters
                        .filter { it.kind == IrParameterKind.Regular }
                        .map { it.name to it.type }

                    ClassResolver(parameters, declaration.returnType)
                }
            }
    }
}
