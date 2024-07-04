package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.*
import tech.mappie.util.*

class ObjectMappingsConstructor(val targetType: IrType, val sources: List<IrValueParameter>) {

    var symbols = listOf<MappieDefinition>()

    var getters = mutableListOf<MappieGetter>()

    var explicit = mutableMapOf<Name, List<ObjectMappingSource>>()

    var constructor: IrConstructor? = null

    val targets
        get() = constructor?.valueParameters ?: emptyList()

    fun construct(): ConstructorCallMapping {
        val mappings: Map<IrValueParameter, List<ObjectMappingSource>> = targets.associateWith { target ->
            val concreteSource = explicit[target.name]

            if (concreteSource != null) {
                concreteSource
            } else {
                val mappings = getters.filter { getter -> getter.name == getterName(target.name) }
                    .flatMap { getter ->
                        val clazz = symbols.singleOrNull { it.fits(getter.type, target.type) }?.clazz
                        val via = when {
                            clazz == null -> null
                            getter.type.isList() && target.type.isList() -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP_LIST }
                            getter.type.isSet() && target.type.isSet() -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP_SET }
                            getter.type.isNullable() && target.type.isNullable() -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP_NULLABLE }
                            else -> clazz.functions.firstOrNull { it.name == IDENTIFIER_MAP }
                        }
                        val viaDispatchReceiver = when {
                            clazz == null -> null
                            clazz.isObject -> IrGetObjectValueImpl(
                                SYNTHETIC_OFFSET,
                                SYNTHETIC_OFFSET,
                                clazz.symbol.defaultType,
                                clazz.symbol
                            )
                            else -> clazz.constructors.firstOrNull { it.valueParameters.isEmpty() }
                                ?.let { irConstructorCall(it) }
                        }
                        listOf(ResolvedSource(getter, via, viaDispatchReceiver))
                    }

                if (mappings.isNotEmpty()) {
                    mappings
                } else if (target.hasDefaultValue() && context.configuration.useDefaultArguments) {
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
            sourceTypes = sources.map { it.type },
            symbol = constructor!!.symbol,
            mappings = mappings,
            unknowns = unknowns,
        )
    }

    fun explicit(entry: Pair<Name, ObjectMappingSource>): ObjectMappingsConstructor =
        apply { explicit.merge(entry.first, listOf(entry.second), Collection<ObjectMappingSource>::plus) }

    companion object {
        fun of(constructor: ObjectMappingsConstructor) =
            ObjectMappingsConstructor(constructor.targetType, constructor.sources).apply {
                getters = constructor.getters
                explicit = constructor.explicit
            }

        fun of(targetType: IrType, sources: List<IrValueParameter>) =
            ObjectMappingsConstructor(targetType, sources)
    }
}