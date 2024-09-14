package tech.mappie.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.parentAsClass
import tech.mappie.api.EnumMappie
import tech.mappie.resolving.classes.ClassResolver
import tech.mappie.resolving.enums.EnumResolver
import tech.mappie.util.isSubclassOf

interface MappingResolver {

    fun resolve(): List<MappingRequest>

    companion object {
        fun of(declaration: IrFunction, context: ResolverContext) =
            when {
                declaration.parentAsClass.isSubclassOf(EnumMappie::class) -> {
                    EnumResolver(declaration, context)
                }
                else -> {
                    ClassResolver(declaration, context)
                }
            }
    }
}
