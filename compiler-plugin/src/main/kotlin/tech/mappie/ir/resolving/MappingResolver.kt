package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.isSubtypeOfClass
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.classes.ClassResolver
import tech.mappie.ir.resolving.classes.ClassUpdateResolver
import tech.mappie.ir.resolving.enums.EnumResolver
import tech.mappie.ir.util.isSubclassOf
import tech.mappie.referenceEnumMappieClass
import tech.mappie.referenceObjectUpdateMappieClass

fun interface MappingResolver {

    fun resolve(function: IrFunction?): List<MappingRequest>

    companion object {
        fun of(source: IrType, target: IrType, context: ResolverContext) =
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
                    EnumResolver(context, declaration.valueParameters.first().type, declaration.returnType)
                }
                declaration.parentAsClass.isSubclassOf(context.referenceObjectUpdateMappieClass()) -> {
                    val source = declaration.valueParameters[0].let { it.name to it.type }
                    val updater = declaration.valueParameters[1].let { it.name to it.type }
                    ClassUpdateResolver(context, source, updater)
                }
                else -> {
                    ClassResolver(context, declaration.valueParameters.map { it.name to it.type }, declaration.returnType)
                }
            }
    }
}
