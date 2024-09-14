package tech.mappie.resolving.classes

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.ifEmpty
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.resolving.*
import tech.mappie.resolving.classes.sources.*
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.resolving.classes.targets.ValueParameterTarget
import tech.mappie.util.*
import tech.mappie.validation.Problem

class ClassMappingRequestBuilder(private val constructor: IrConstructor, private val context: ResolverContext) {

    private val targets = mutableListOf<ClassMappingTarget>()

    private val implicit = mutableMapOf<Name, List<ImplicitClassMappingSource>>()

    private val explicit = mutableMapOf<Name, List<ExplicitClassMappingSource>>()

    fun construct(origin: IrFunction): ClassMappingRequest {
        val mappings = targets.associateWith { target ->
            explicit(target) ?: implicit(target) // TODO: we should add all and select later
        }

        val unknowns = explicit.filterKeys { name ->
            targets.none { it.name == name }
        }

        return ClassMappingRequest(
            origin,
            constructor,
            mappings,
            unknowns,
        )
    }

    private fun explicit(target: ClassMappingTarget): List<ExplicitClassMappingSource>? =
        explicit[target.name]?.let { sources ->
            sources.map { source ->
                if (source is ExplicitPropertyMappingSource && source.transformation == null && !source.type.isMappableFrom(target.type)) {
                    val transformation = findTransformation(source, target)
                    if (transformation != null) {
                        source.copy(transformation = transformation)
                    } else {
                        source
                    }
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
                    val transformation = findTransformation(source, target)
                    if (transformation != null) {
                        when (source) {
                            is ImplicitPropertyMappingSource -> source.copy(transformation = transformation)
                            else -> throw MappiePanicException("Only ImplicitPropertyMappingSource should occur when resolving a transformation.")
                        }
                    } else {
                        source
                    }
                }
            }.ifEmpty {
                if (target is ValueParameterTarget && target.value.hasDefaultValue() && context.configuration.useDefaultArguments) {
                    listOf(ParameterDefaultValueMappingSource(target.value))
                } else {
                    emptyList()
                }
            }
        }

    private fun findTransformation(source: ClassMappingSource, target: ClassMappingTarget): PropertyMappingViaMapperTransformation? {
        val mappers = context.definitions.matching(source.type, target.type)
        return if (mappers.size == 1) {
            PropertyMappingViaMapperTransformation(mappers.single(), null)
        } else if (mappers.size > 1) {
            val error = Problem.error(
                "Multiple mappers resolved to be used in an implicit via",
                location(context.function!!),
                listOf(
                    "Call one of ${mappers.joinToString { it.clazz.name.asString() }} explicitly.",
                    "Delete all except one of ${mappers.joinToString { it.clazz.name.asString() }}.",
                )
            )
            context.log(error)
            PropertyMappingViaMapperTransformation(mappers.first(), null)
        } else {
            null
        }
    }

    fun explicit(entry: Pair<Name, ExplicitClassMappingSource>): ClassMappingRequestBuilder =
        apply { explicit.merge(entry.first, listOf(entry.second), Collection<ExplicitClassMappingSource>::plus) }

    fun sources(sources: Map<Name, ImplicitClassMappingSource>) =
        apply { sources.forEach { (name, source) -> implicit.merge(name, listOf(source), List<ImplicitClassMappingSource>::plus) } }

    fun targets(targets: List<ClassMappingTarget>) =
        apply { this.targets.addAll(targets) }
}
