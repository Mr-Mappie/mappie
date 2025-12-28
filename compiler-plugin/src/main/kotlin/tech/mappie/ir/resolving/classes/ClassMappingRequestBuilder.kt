package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.backend.jvm.ir.upperBound
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.ifEmpty
import tech.mappie.ir.MappieContext
import tech.mappie.config.options.NamingConventionMode
import tech.mappie.config.options.namingConvention
import tech.mappie.config.options.useDefaultArguments
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.PrioritizationMap.Companion.prioritize
import tech.mappie.ir.resolving.*
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.sources.FunctionMappingSource
import tech.mappie.ir.resolving.classes.sources.ImplicitClassMappingSource
import tech.mappie.ir.resolving.classes.sources.ImplicitPropertyMappingSource
import tech.mappie.ir.resolving.classes.sources.ParameterDefaultValueMappingSource
import tech.mappie.ir.resolving.classes.sources.ParameterValueMappingSource
import tech.mappie.ir.resolving.classes.sources.PropertyMappingViaLocalMethodTransformation
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.util.isPrimitive
import tech.mappie.ir.util.isSubtypeOf
import tech.mappie.ir.util.location
import tech.mappie.util.normalize

class ClassMappingRequestBuilder(private val constructor: IrConstructor) {

    private val targets = mutableListOf<ClassMappingTarget>()

    private val sources = mutableMapOf<Name, IrType>()

    private val implicit = mutableMapOf<Name, List<ImplicitClassMappingSource>>()

    private val explicit = mutableMapOf<Name, List<ExplicitClassMappingSource>>()

    context(context: MappieContext)
    fun construct(origin: InternalMappieDefinition): ClassMappingRequest {
        val useDefaultArguments = useDefaultArguments(origin.referenceMapFunction())
        val namingConvention = namingConvention(origin.referenceMapFunction())

        val normalizedImplicit = if (namingConvention == NamingConventionMode.LENIENT) buildNormalizedLookup() else null

        val mappings = targets.associateWith { target ->
            explicit(origin, target) ?: implicit(origin, target, useDefaultArguments, normalizedImplicit)
        }

        return ClassMappingRequest(origin, sources.map { it.value }, constructor, TargetSourcesClassMappings(mappings))
    }

    private fun buildNormalizedLookup(): Map<String, List<ImplicitClassMappingSource>> =
        implicit.flatMap { (name, sources) ->
            sources.map { source -> name.normalize() to source }
        }.groupBy({ it.first }, { it.second })

    context(context: MappieContext)
    private fun explicit(origin: InternalMappieDefinition, target: ClassMappingTarget): List<ExplicitClassMappingSource>? =
        explicit[target.name]?.let { sources ->
            sources.map { source ->
                if (source is ExplicitPropertyMappingSource && source.transformation == null && !target.type.isSubtypeOf(source.type)) {
                    source.copy(transformation = transformation(origin, source, target))
                } else {
                    source
                }
            }
        }

    context(context: MappieContext)
    private fun implicit(
        origin: InternalMappieDefinition,
        target: ClassMappingTarget,
        useDefaultArguments: Boolean,
        normalizedImplicit: Map<String, List<ImplicitClassMappingSource>>?
    ): List<ImplicitClassMappingSource> {
        // First try exact match
        val exactMatch = implicit.getOrDefault(target.name, emptyList())

        // If exact match found or case-insensitive matching disabled, use exact match
        val sources = if (exactMatch.isNotEmpty() || normalizedImplicit == null) {
            exactMatch
        } else {
            // Try case-insensitive matching
            val normalizedTarget = target.name.normalize()
            normalizedImplicit.getOrDefault(normalizedTarget, emptyList())
        }

        return sources.map { source ->
            if (source.type.isSubtypeOf(target.type)) {
                source
            } else {
                when (source) {
                    is ImplicitPropertyMappingSource -> source.copy(transformation = transformation(origin, source, target))
                    is FunctionMappingSource -> source.copy(transformation = transformation(origin, source, target))
                    is ParameterValueMappingSource -> source.copy(transformation = transformation(origin, source, target))
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

    context(context: MappieContext)
    private fun transformation(origin: InternalMappieDefinition, source: ClassMappingSource, target: ClassMappingTarget): PropertyMappingTransformation? {
        // 1. Check local conversion methods first (highest priority)
        val localMethod = origin.localConversions.matching(source.type, target.type).firstOrNull()
        if (localMethod != null) {
            return PropertyMappingViaLocalMethodTransformation(localMethod, target.type)
        }

        // 2. Fall back to existing mapper lookup
        val mappers = context.definitions.matching(origin, source.type, target.type)

        val prioritized = mappers.prioritize(source.type, target.type)
        val selected = prioritized.select()

        return when {
            selected != null -> {
                PropertyMappingViaMapperTransformation(selected, null, target.type)
            }
            prioritized.size > 1 -> {
                val location = when (source) {
                    is ExplicitClassMappingSource -> location(origin.referenceMapFunction().fileEntry, source.origin)
                    else -> location(origin.referenceMapFunction())
                }
                val error = Problem.error("Multiple mappers resolved to be used in an implicit via", location)
                context.logger.log(error)
                null
            }
            !source.type.isPrimitive() && !target.type.isPrimitive() -> {
                GeneratedViaMapperTransformation(source, target)
            }
            else -> {
                null
            }
        }
    }

    fun explicit(entry: Pair<Name, ExplicitClassMappingSource>): ClassMappingRequestBuilder = apply {
        explicit.merge(entry.first, listOf(entry.second), List<ExplicitClassMappingSource>::plus)
    }

    context(context: MappieContext)
    fun sources(parameters: List<Pair<Name, IrType>>) = apply {
        sources.putAll(parameters)
        parameters.map { (name, type) ->
            implicit.merge(name, listOf(ParameterValueMappingSource(name, type, null)), List<ImplicitClassMappingSource>::plus)
            type.upperBound.getClass()!!.accept(ImplicitClassMappingSourcesCollector(context), name to type).forEach { (name, source) ->
                implicit.merge(name, listOf(source), List<ImplicitClassMappingSource>::plus)
            }
        }
    }

    fun targets(targets: List<ClassMappingTarget>) = apply {
        this.targets.addAll(targets)
    }
}
