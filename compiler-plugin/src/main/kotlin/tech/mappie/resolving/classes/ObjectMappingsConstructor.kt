package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.*
import tech.mappie.resolving.classes.sources.MappieSource
import tech.mappie.resolving.classes.targets.MappieSetterTarget
import tech.mappie.resolving.classes.targets.MappieTarget
import tech.mappie.resolving.classes.targets.MappieValueParameterTarget
import tech.mappie.util.*

class ObjectMappingsConstructor(val targetType: IrType, val sources: List<IrValueParameter>) {

    var symbols = listOf<MappieDefinition>()

    var getters = mutableListOf<MappieSource>()

    var explicit = mutableMapOf<Name, List<ObjectMappingSource>>()

    var constructor: IrConstructor? = null

    val targets: List<MappieTarget>
        get() = constructParameters + setters

    val constructParameters: List<MappieTarget>
        get() = constructor?.valueParameters?.map { MappieValueParameterTarget(it) } ?: emptyList()

    val setters: Sequence<MappieTarget>
        get() = targetType.classOrFail.owner.properties
            .filter { it.setter != null && it.name.toString() !in constructParameters.map { it.name.toString() } }
            .map { MappieSetterTarget(it) }

    fun construct(): ConstructorCallMapping {
        val mappings: Map<MappieTarget, List<ObjectMappingSource>> = targets.associateWith { target ->
            val concreteSource = explicit[target.name]

            if (concreteSource != null) {
                concreteSource
            } else {
                val mappings = getters.filter { getter -> getter.name == getterName(target.name) }
                    .flatMap { getter ->
                        val clazz = symbols.singleOrNull { it.fits(getter.type, target.type) }?.clazz
                        val via: Pair<IrClass, IrSimpleFunction>? = when {
                            clazz == null -> null
                            getter.type.isList() && target.type.isList() -> clazz to clazz.functions.first { it.name == IDENTIFIER_MAP_LIST }
                            getter.type.isSet() && target.type.isSet() -> clazz to clazz.functions.first { it.name == IDENTIFIER_MAP_SET }
                            getter.type.isNullable() && target.type.isNullable() -> clazz to clazz.functions.first { it.name == IDENTIFIER_MAP_NULLABLE }
                            else -> clazz to clazz.functions.first { it.name == IDENTIFIER_MAP }
                        }
                        listOf(ResolvedSource(getter, via))
                    }

                if (mappings.isNotEmpty()) {
                    mappings
                } else if (target is MappieValueParameterTarget && target.value.hasDefaultValue() && context.configuration.useDefaultArguments) {
                    listOf(ValueSource(target.value.defaultValue!!.expression, null))
                } else {
                    emptyList()
                }
            }
        }.filter { it.key is MappieValueParameterTarget || (it.key is MappieSetterTarget && it.value.isNotEmpty()) }

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