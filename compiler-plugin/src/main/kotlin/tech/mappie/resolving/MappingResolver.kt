package tech.mappie.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isSubtypeOfClass
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.api.EnumMappie
import tech.mappie.resolving.classes.ClassResolver
import tech.mappie.resolving.enums.EnumResolver
import tech.mappie.util.isSubclassOf

fun interface MappingResolver {

    fun resolve(body: IrBody?): List<MappingRequest>

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
                declaration.parentAsClass.isSubclassOf(EnumMappie::class) -> {
                    EnumResolver(context, declaration.valueParameters.first().type, declaration.returnType)
                }
                else -> {
                    ClassResolver(context, declaration.valueParameters.map { it.name to it.type }, declaration.returnType)
                }
            }
    }
}
