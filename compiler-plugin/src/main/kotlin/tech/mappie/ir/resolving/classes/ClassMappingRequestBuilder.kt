package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.backend.jvm.ir.upperBound
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.model.eraseContainingTypeParameters
import org.jetbrains.kotlin.utils.ifEmpty
import tech.mappie.ir.MappieContext
import tech.mappie.config.options.useDefaultArguments
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.PrioritizationMap
import tech.mappie.ir.PrioritizationMap.Companion.prioritize
import tech.mappie.ir.resolving.*
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.sources.FunctionMappingSource
import tech.mappie.ir.resolving.classes.sources.ImplicitClassMappingSource
import tech.mappie.ir.resolving.classes.sources.ImplicitPropertyMappingSource
import tech.mappie.ir.resolving.classes.sources.ParameterDefaultValueMappingSource
import tech.mappie.ir.resolving.classes.sources.ParameterValueMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.util.isPrimitive
import tech.mappie.ir.util.isSubtypeOf
import tech.mappie.ir.util.location

class ClassMappingRequestBuilder(private val constructor: IrConstructor) {

    private val targets = mutableListOf<ClassMappingTarget>()

    private val sources = mutableMapOf<Name, IrType>()

    private val implicit = mutableMapOf<Name, List<ImplicitClassMappingSource>>()

    private val explicit = mutableMapOf<Name, List<ExplicitClassMappingSource>>()

    context(context: MappieContext)
    fun construct(origin: InternalMappieDefinition): ClassMappingRequest {
        val useDefaultArguments = context.useDefaultArguments(origin.referenceMapFunction())

        val mappings = targets.associateWith { target ->
            explicit(origin, target) ?: implicit(origin, target, useDefaultArguments)
        }

        return ClassMappingRequest(origin, sources.map { it.value }, constructor, mappings)
    }

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
    private fun implicit(origin: InternalMappieDefinition, target: ClassMappingTarget, useDefaultArguments: Boolean): List<ImplicitClassMappingSource> =
        implicit.getOrDefault(target.name, emptyList()).let { sources ->
            sources.map { source ->
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
        val mappers = context.definitions.matching(source.type, target.type)
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
            // TODO: which branch?
            !source.type.isPrimitive() && !target.type.isPrimitive() -> {
                GeneratedViaMapperTransformation(source, target, origin.clazz)
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
