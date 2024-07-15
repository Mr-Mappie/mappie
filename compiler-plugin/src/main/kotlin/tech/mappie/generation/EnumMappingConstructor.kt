package tech.mappie.generation

import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.EnumMapping
import tech.mappie.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.util.irGetEnumValue

class EnumMappingConstructor(private val mapping: EnumMapping, declaration: IrFunction)
    : MappingConstructor(declaration) {

    override fun construct(scope: Scope) =
        context.blockBody(scope) {
            +irReturn(irWhen(mapping.targetType, mapping.mappings
                .filter { (_, targets) -> targets.isNotEmpty() }
                .map { (source, targets) ->
                    val lhs = irGet(declaration.valueParameters.first())
                    val rhs = irGetEnumValue(mapping.targetType, source.symbol)
                    val result: IrExpression = when (val target = targets.single()) {
                        is ExplicitEnumMappingTarget -> target.target
                        is ResolvedEnumMappingTarget -> irGetEnumValue(mapping.targetType, target.target.symbol)
                        is ThrowingEnumMappingTarget -> irThrow(target.exception)
                    }
                    irBranch(irEqeqeq(lhs, rhs), result)
                } + irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol))))
        }
}