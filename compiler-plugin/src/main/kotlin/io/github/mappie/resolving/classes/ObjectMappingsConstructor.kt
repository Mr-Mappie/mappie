package io.github.mappie.resolving.classes

import io.github.mappie.resolving.ConstructorCallMapping
import io.github.mappie.util.getterName
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.hasDefaultValue
import org.jetbrains.kotlin.name.Name

class ObjectMappingsConstructor(val targetType: IrType, val source: IrValueParameter) {

    var getters = mutableListOf<IrSimpleFunction>()

    // TODO: Map of lists for detecting duplicates
    var explicit = mutableListOf<Pair<Name, ObjectMappingSource>>()

    var constructor: IrConstructor? = null

    val targets
        get() = constructor?.valueParameters ?: emptyList()

    fun construct(): ConstructorCallMapping {
        val mappings = targets.associateWith { target ->
            val concreteSource = explicit.firstOrNull { it.first == target.name }

            if (concreteSource != null) {
                listOf(concreteSource.second)
            } else {
                val getter = getters.firstOrNull { getter ->
                    getter.name == getterName(target.name)
                }
                if (getter != null) {
                    listOf(PropertySource(getter.symbol, target.type, source.symbol, true))
                } else if (target.hasDefaultValue()) {
                    listOf(DefaultParameterValueSource(target.defaultValue!!.expression))
                } else {
                    emptyList()
                }
            }
        }

        val unknowns = explicit
            .filter { it.first !in mappings.map { it.key.name } }

        return ConstructorCallMapping(
            targetType = targetType,
            sourceType = source.type,
            symbol = constructor!!.symbol,
            mappings = mappings,
            unknowns = unknowns,
        )
    }

    fun explicit(entry: Pair<Name, ObjectMappingSource>): ObjectMappingsConstructor =
        apply { explicit.add(entry) }

    companion object {
        fun of(constructor: ObjectMappingsConstructor) =
            ObjectMappingsConstructor(constructor.targetType, constructor.source).apply {
                getters = constructor.getters
                explicit = constructor.explicit
            }

        fun of(targetType: IrType, source: IrValueParameter) =
            ObjectMappingsConstructor(targetType, source)
    }
}