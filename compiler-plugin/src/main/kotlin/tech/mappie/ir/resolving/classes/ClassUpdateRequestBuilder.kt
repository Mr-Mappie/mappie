package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
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

class ClassUpdateRequestBuilder(private val context: ResolverContext)
    : TargetAccumulator {

    private val targets = mutableListOf<ClassMappingTarget>()

    private val implicit = mutableMapOf<Name, List<ImplicitClassMappingSource>>()

    private val explicit = mutableMapOf<Name, List<ExplicitClassMappingSource>>()

    fun construct(origin: IrFunction): ClassUpdateRequest {
        val mappings = targets.associateWith { target ->
            explicit(target) ?: implicit(target) // TODO: we should add all and select later
        }

        return ClassUpdateRequest(
            origin,
            origin.returnType, // TODO: check
            origin.valueParameters.first().name,
            mappings,
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

    private fun implicit(target: ClassMappingTarget): List<ImplicitClassMappingSource> =
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

    override fun explicit(entry: Pair<Name, ExplicitClassMappingSource>) {
        explicit.merge(entry.first, listOf(entry.second), List<ExplicitClassMappingSource>::plus)
    }

    fun updater(updater: Pair<Name, IrType>) = apply {
        updater.second.getClass()!!.accept(ImplicitClassMappingSourcesCollector(), updater).forEach { (name, source) ->
            implicit.merge(name, listOf(source), List<ImplicitClassMappingSource>::plus)
        }
    }

    fun targets(targets: List<ClassMappingTarget>) =
        apply { this.targets.addAll(targets) }
}
