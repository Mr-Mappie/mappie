package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.*
import tech.mappie.resolving.classes.sources.MappieSource
import tech.mappie.resolving.classes.targets.MappieTarget
import tech.mappie.resolving.classes.targets.MappieValueParameterTarget
import tech.mappie.util.*

class ObjectMappingsConstructor(
    private val symbols: List<MappieDefinition>,
    private val constructor: IrConstructor,
    private val sources: List<MappieSource>,
    private val targets: List<MappieTarget>,
    private val explicit: MutableMap<Name, List<ObjectMappingSource>> = mutableMapOf(),
) {

    private val targetType = constructor.returnType

    fun construct(): ConstructorCallMapping {
        val mappings: Map<MappieTarget, List<ObjectMappingSource>> = targets.associateWith { target ->
            val concreteSource = explicit[target.name]

            if (concreteSource != null) {
                concreteSource
            } else {
                val mappings = sources.filter { source -> source.name == getterName(target.name) }
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
        }.filter { it.key is MappieValueParameterTarget || it.value.isNotEmpty() }

        val unknowns = explicit
            .filter { it.key !in mappings.map { it.key.name } }

        return ConstructorCallMapping(
            targetType = targetType,
            sourceTypes = sources.map { it.type },
            symbol = constructor.symbol,
            mappings = mappings,
            unknowns = unknowns,
        )
    }

    fun explicit(entry: Pair<Name, ObjectMappingSource>): ObjectMappingsConstructor =
        apply { explicit.merge(entry.first, listOf(entry.second), Collection<ObjectMappingSource>::plus) }
}