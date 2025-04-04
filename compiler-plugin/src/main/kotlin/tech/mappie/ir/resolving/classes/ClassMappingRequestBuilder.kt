package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.backend.jvm.ir.upperBound
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.ifEmpty
import tech.mappie.config.options.useDefaultArguments
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.resolving.*
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.sources.FunctionMappingSource
import tech.mappie.ir.resolving.classes.sources.ImplicitClassMappingSource
import tech.mappie.ir.resolving.classes.sources.ImplicitPropertyMappingSource
import tech.mappie.ir.resolving.classes.sources.ParameterDefaultValueMappingSource
import tech.mappie.ir.resolving.classes.sources.ParameterValueMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget
import tech.mappie.util.*
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.util.isMappableFrom
import tech.mappie.ir.util.isPrimitive
import tech.mappie.ir.util.location

class ClassMappingRequestBuilder(private val constructor: IrConstructor, private val context: ResolverContext) {

    private val targets = mutableListOf<ClassMappingTarget>()

    private val sources = mutableMapOf<Name, IrType>()

    private val implicit = mutableMapOf<Name, List<ImplicitClassMappingSource>>()

    private val explicit = mutableMapOf<Name, List<ExplicitClassMappingSource>>()

    fun construct(origin: IrFunction): ClassMappingRequest {
        val useDefaultArguments = context.useDefaultArguments(origin)

        val mappings = targets.associateWith { target ->
            explicit(target) ?: implicit(target, useDefaultArguments) // TODO: we should add all and select later
        }
        val unknowns = explicit.filterKeys { name ->
            targets.none { it.name == name }
        }

        return ClassMappingRequest(
            origin,
            sources.map { it.value },
            constructor,
            mappings,
            unknowns,
        )
    }

    private fun explicit(target: ClassMappingTarget): List<ExplicitClassMappingSource>? =
        explicit[target.name]?.let { sources ->
            sources.map { source ->
                if (source is ExplicitPropertyMappingSource && source.transformation == null && !source.type.isMappableFrom(target.type)) {
                    source.copy(transformation = transformation(source, target))
                } else {
                    source
                }
            }
        }

    private fun implicit(target: ClassMappingTarget, useDefaultArguments: Boolean): List<ImplicitClassMappingSource> =
        implicit.getOrDefault(target.name, emptyList()).let { sources ->
            sources.map { source ->
                if (source.type.isMappableFrom(target.type)) {
                    source
                } else {
                    when (source) {
                        is ImplicitPropertyMappingSource -> source.copy(transformation = transformation(source, target))
                        is FunctionMappingSource -> source.copy(transformation = transformation(source, target))
                        is ParameterValueMappingSource -> source.copy(transformation = transformation(source, target))
                        is ParameterDefaultValueMappingSource -> panic("ParameterDefaultValueMappingSource should not occur when resolving a transformation.")
                    }
                }
            }.ifEmpty {
                if (target is ValueParameterTarget && target.value.hasDefaultValue() && useDefaultArguments) {
                    listOf(ParameterDefaultValueMappingSource(target.value))
                } else {
                    emptyList()
                }
            }
        }

    private fun transformation(source: ClassMappingSource, target: ClassMappingTarget): PropertyMappingTransformation? {
        val mappers = context.definitions.matching(source.type, target.type)
        return when {
            mappers.size == 1 -> {
                PropertyMappingViaMapperTransformation(mappers.single(), null)
            }
            mappers.size > 1 -> {
                val location = when (source) {
                    is ExplicitClassMappingSource -> location(context.function!!.fileEntry, source.origin)
                    else -> location(context.function!!)
                }
                val error = Problem.error(
                    "Multiple mappers resolved to be used in an implicit via",
                    location,
                    listOf(
                        "Call one of ${mappers.joinToString { it.clazz.name.asString() }} explicitly.",
                        "Delete all except one of ${mappers.joinToString { it.clazz.name.asString() }}.",
                    )
                )
                context.logger.log(error)
                PropertyMappingViaMapperTransformation(mappers.first(), null)
            }
            !source.type.isPrimitive() && !target.type.isPrimitive() -> {
                GeneratedViaMapperTransformation(source, target)
            }
            else -> {
                null
            }
        }
    }

    fun explicit(entry: Pair<Name, ExplicitClassMappingSource>): ClassMappingRequestBuilder =
        apply { explicit.merge(entry.first, listOf(entry.second), List<ExplicitClassMappingSource>::plus) }

    fun sources(entries: List<Pair<Name, IrType>>) = apply {
        sources.putAll(entries)
        entries.map { (name, type) ->
            implicit.merge(name, listOf(ParameterValueMappingSource(name, type, null)), List<ImplicitClassMappingSource>::plus)

            type.upperBound.getClass()!!.accept(ImplicitClassMappingSourcesCollector(), name to type).forEach { (name, source) ->
                implicit.merge(name, listOf(source), List<ImplicitClassMappingSource>::plus)
            }
        }
    }

    fun targets(targets: List<ClassMappingTarget>) =
        apply { this.targets.addAll(targets) }
}
