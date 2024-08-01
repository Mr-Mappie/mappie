package tech.mappie.validation.problems

import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.resolving.classes.*
import tech.mappie.resolving.classes.targets.MappieTarget
import tech.mappie.validation.Problem

class MultipleTransformationsProblems(private val mappings: Map<MappieTarget, List<ObjectMappingSource>>) {
    fun all(): List<Problem> =
        mappings.map { (_, sources) ->
            when (val source = sources.single()) {
                is ResolvedSource -> source.transformation
                is PropertySource -> source.transformation
                else -> null
            }
        }
            .filterNotNull()
            .map { transformations ->
                val names = transformations.map { it as MappieViaResolved }.joinToString { it.definition.clazz.name.asString() }
                    Problem.error(
                        "Multiple mappers resolved to be used in an implicit via",
                        suggestions = listOf(
                            "Explicitly call one of $names explicitly.",
                            "Delete all except one of $names."
                        ),
                    )
            }

    companion object {
        fun of(mapping: ConstructorCallMapping): MultipleTransformationsProblems =
            MultipleTransformationsProblems(
                mapping.mappings
                    .filter { it.value.size == 1 }
                    .filter {
                        (it.value.single().let { it is ResolvedSource && it.transformation.size > 1 }) ||
                        (it.value.single().let { it is PropertySource && it.transformation.size > 1 })
                    }
            )
    }
}