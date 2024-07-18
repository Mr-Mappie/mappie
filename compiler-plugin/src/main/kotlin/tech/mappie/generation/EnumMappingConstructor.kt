package tech.mappie.generation

import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.impl.IrBranchImpl
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.EnumMapping
import tech.mappie.resolving.enums.EnumMappingTarget
import tech.mappie.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.util.referenceFunctionValueOf

class EnumMappingConstructor(private val mapping: EnumMapping, declaration: IrFunction)
    : MappingConstructor(declaration) {

    override fun construct(scope: Scope) =
        context.blockBody(scope) {
            val branches = mapping.mappings
                .filter { (_, targets) -> targets.isNotEmpty() }
                .map { generateMapping(it.key, it.value.single()) }

            +irReturn(irWhen(mapping.targetType, branches + generateElseBranch()))
        }

    private fun IrBlockBodyBuilder.generateMapping(source: IrEnumEntry, target: EnumMappingTarget): IrBranchImpl {
        val lhs = irGet(declaration.valueParameters.first())
        val rhs = irCall(source.referenceFunctionValueOf()).apply {
            putValueArgument(0, irString(source.name.asString()))
        }
        return irBranch(irEquals(lhs, rhs), when (target) {
                is ExplicitEnumMappingTarget -> target.target
                is ResolvedEnumMappingTarget -> irCall(target.target.referenceFunctionValueOf()).apply {
                    putValueArgument(0, irString(target.target.name.asString()))
                }
                is ThrowingEnumMappingTarget -> irThrow(target.exception)
            }
        )
    }

    private fun IrBlockBodyBuilder.generateElseBranch() =
        irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol))
}