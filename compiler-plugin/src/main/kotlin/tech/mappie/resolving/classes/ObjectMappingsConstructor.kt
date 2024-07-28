package tech.mappie.resolving.classes

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
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

    fun construct(origin: IrElement): ConstructorCallMapping {
        val generatedMappers = mutableSetOf<GeneratedMappieClass>()
        val mappings = targets.associateWith { target ->
            val concreteSource = explicit[target.name]
            if (concreteSource != null) {
                val source = concreteSource.singleOrNull()
                if (source != null && source is PropertySource && source.transformation == null) {
                    if (!target.type.isAssignableFrom(source.type)) {
                        val via = symbols.firstOrNull { it.fits(source.type, target.type) }
                            ?.let { via -> when {
                                target.type.isList() -> via.copy(toType = context.irBuiltIns.listClass.typeWith(listOf(via.toType)))
                                target.type.isSet() -> via.copy(toType = context.irBuiltIns.setClass.typeWith(listOf(via.toType)))
                                else -> via
                            } }
                        listOf(source.copy(transformation = via?.let{ MappieViaResolved(it) }))
                    } else {
                        concreteSource
                    }
                } else {
                    concreteSource
                }
            } else {
                val mappings = sources.filter { source -> source.name == getterName(target.name) }
                    .map { getter ->
                        if (target.type.isAssignableFrom(getter.type)) {
                            ResolvedSource(getter, null, null, origin)
                        } else {
                            val transformation = symbols
                                .singleOrNull { it.fits(getter.type, target.type) }
                                ?.let { MappieViaResolved(it) }
                                ?: tryGenerateMapper(getter.type, target.type)
                                    ?.also { generatedMappers.add(it) }
                                    ?.let { MappieViaGeneratedClass(it) }

                            ResolvedSource(
                                getter,
                                transformation,
                                transformation?.let { target.type },
                                origin,
                            )
                        }
                    }

                if (mappings.isNotEmpty()) {
                    mappings
                } else if (target is MappieValueParameterTarget && target.value.hasDefaultValue() && context.configuration.useDefaultArguments) {
                    listOf(DefaultArgumentSource(target.value.type, origin))
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
            generated = generatedMappers,
            unknowns = unknowns,
        )
    }

    private fun tryGenerateMapper(source: IrType, target: IrType): GeneratedMappieClass? {
        return if (source.classOrNull?.owner?.kind == ClassKind.ENUM_CLASS && target.classOrNull?.owner?.kind == ClassKind.ENUM_CLASS) {
            val sourceEntries = source.classOrFail.owner.declarations.filterIsInstance<IrEnumEntry>()
            val targetEntries = target.classOrFail.owner.declarations.filterIsInstance<IrEnumEntry>()

            if (sourceEntries.all { it.name in targetEntries.map { it.name } }) {
                val name = Name.identifier(source.classOrFail.owner.name.asString() + "To" + target.classOrFail.owner.name.asString() + "Mapper")
                GeneratedMappieEnumClass(name, source, sourceEntries, target, targetEntries)
            } else {
                null
            }
        } else {
            null
        }
    }

    fun explicit(entry: Pair<Name, ObjectMappingSource>): ObjectMappingsConstructor =
        apply { explicit.merge(entry.first, listOf(entry.second), Collection<ObjectMappingSource>::plus) }
}
