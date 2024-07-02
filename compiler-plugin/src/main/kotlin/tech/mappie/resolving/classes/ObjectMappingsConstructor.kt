package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.resolving.*
import tech.mappie.util.*

class ObjectMappingsConstructor(val targetType: IrType, val source: IrValueParameter) {

    var symbols = listOf<MappieDefinition>()

    var getters = mutableListOf<IrSimpleFunction>()

    var explicit = mutableMapOf<Name, List<ObjectMappingSource>>()

    var constructor: IrConstructor? = null

    val targets
        get() = constructor?.valueParameters ?: emptyList()

    fun construct(): ConstructorCallMapping {
        val mappings = targets.associateWith { target ->
            val concreteSource = explicit[target.name]

            if (concreteSource != null) {
                concreteSource
            } else {
                val getter = getters.firstOrNull { getter ->
                    getter.name == getterName(target.name)
                }
                if (getter != null) {
                    val clazz = symbols.singleOrNull { it.fits(getter.returnType, target.type) }?.clazz
                    val via = when {
                        clazz == null -> null
                        getter.returnType.isList() && target.type.isList() -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP_LIST }
                        getter.returnType.isSet() && target.type.isSet() -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP_SET }
                        getter.returnType.isNullable() && target.type.isNullable() -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP_NULLABLE }
                        else -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP }
                    }
                    val viaDispatchReceiver = when {
                        clazz == null -> null
                        clazz.isObject -> IrGetObjectValueImpl(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, clazz.symbol.defaultType, clazz.symbol)
                        else ->  clazz.constructors.firstOrNull { it.valueParameters.isEmpty() }?.let { irConstructorCall(it) }
                    }
                    listOf(ResolvedSource(getter.symbol, irGet(source), via, viaDispatchReceiver))
                }  else if (target.hasDefaultValue()) {
                    listOf(ValueSource(target.defaultValue!!.expression, null))
                } else {
                    emptyList()
                }
            }
        }

        val unknowns = explicit
            .filter { it.key !in mappings.map { it.key.name } }

        return ConstructorCallMapping(
            targetType = targetType,
            sourceType = source.type,
            symbol = constructor!!.symbol,
            mappings = mappings,
            unknowns = unknowns,
        )
    }

    fun explicit(entry: Pair<Name, ObjectMappingSource>): ObjectMappingsConstructor =
        apply { explicit.merge(entry.first, listOf(entry.second), Collection<ObjectMappingSource>::plus) }

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