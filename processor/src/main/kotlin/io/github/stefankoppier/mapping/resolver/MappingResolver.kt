package io.github.stefankoppier.mapping.resolver

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.properties

class MappingResolver {

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    fun resolve(source: IrType, target: IrType): List<Pair<IrType, IrProperty>> {
        val targetClass = requireNotNull(target.getClass()) {
            "Expected return type of map to be non-null."
        }
        val primaryConstructor = requireNotNull(targetClass.primaryConstructor) {
            "The target type must have a primary constructor."
        }
        val targets = primaryConstructor.valueParameters

        val sourceClass = requireNotNull(source.getClass()) {
            "Expected type of source argument to be non-null."
        }
        val sourceValues = sourceClass.properties

        val arguments: List<Pair<IrType, IrProperty>> = targets.map { target ->
            target.type to sourceValues.first { it.name == target.name }
        }
        return arguments
    }
}