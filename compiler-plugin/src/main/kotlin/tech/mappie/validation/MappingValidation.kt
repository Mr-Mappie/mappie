package tech.mappie.validation

import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.resolving.EnumMapping
import tech.mappie.resolving.Mapping
import tech.mappie.resolving.classes.PropertySource
import tech.mappie.util.isAssignableFrom
import tech.mappie.util.location
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.types.removeAnnotations
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.resolving.classes.ExpressionSource
import tech.mappie.resolving.classes.ResolvedSource
import tech.mappie.resolving.classes.ValueSource
import tech.mappie.util.dumpKotlinLike

data class Problem(
    val description: String,
    val severity: Severity,
    val location: CompilerMessageLocation?
) {
    enum class Severity { ERROR, WARNING; }

    companion object {
        fun error(description: String, location: CompilerMessageLocation? = null) =
            Problem(description, Severity.ERROR, location)

        fun warning(description: String, location: CompilerMessageLocation? = null) =
            Problem(description, Severity.WARNING, location)
    }
}

interface MappingValidation {
    val problems: List<Problem>

    fun isValid(): Boolean =
        problems.none { it.severity == Problem.Severity.ERROR }

    fun warnings(): List<Problem> =
        problems.filter { it.severity == Problem.Severity.WARNING }

    private class ConstructorCallMappingValidation(private val file: IrFileEntry, private val mapping: ConstructorCallMapping) : MappingValidation {

        override val problems: List<Problem> =
            buildList {
                addAll(
                    mapping.mappings
                        .filter { (_, sources) -> sources.size != 1 }
                        .map { (target, sources) ->
                            Problem.error("Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}")
                        }
                )

                addAll(
                    mapping.mappings
                        .filter { (_, sources) -> sources.size == 1 }
                        .filter { (target, sources) -> !target.type.isAssignableFrom(sources.single().type, true) }
                        .map { (target, sources) ->
                            when (val source = sources.single()) {
                                is PropertySource -> {
                                    val location = location(file, source.origin)
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} of type ${target.type.dumpKotlinLike()} cannot be assigned from ${source.property.dumpKotlinLike()} of type ${source.type.dumpKotlinLike()}"
                                    Problem.error(description, location)
                                }
                                is ExpressionSource -> {
                                    val location = location(file, source.origin)
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} of type ${target.type.dumpKotlinLike()} cannot be assigned from expression of type ${source.type.dumpKotlinLike()}"
                                    Problem.error(description, location)
                                }
                                is ResolvedSource -> {
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} automatically resolved from ${source.property.dumpKotlinLike()} but cannot assign source type ${source.type.dumpKotlinLike()} to target type ${target.type.dumpKotlinLike()}"
                                    Problem.error(description, null)
                                }
                                is ValueSource -> {
                                    val location = source.origin?.let { location(file, it) }
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} of type ${target.type.dumpKotlinLike()} cannot be assigned from value of type ${source.type.dumpKotlinLike()}"
                                    Problem.error(description, location)
                                }
                            }
                        }
                )

                addAll(
                    mapping.mappings
                        .filter { (_, sources) -> sources.size == 1 }
                        .filter { (target, sources) ->
                            target.type.isAssignableFrom(sources.single().type, true) &&
                            !target.type.isAssignableFrom(sources.single().type, false) }
                        .map { (target, sources) ->
                            when (val source = sources.single()) {
                                is PropertySource -> {
                                    val location = location(file, source.origin)
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} of type ${target.type.dumpKotlinLike()} is unsafe to from ${source.property.dumpKotlinLike()} of platform type ${source.type.removeAnnotations().dumpKotlinLike()}"
                                    Problem.warning(description, location)
                                }
                                is ExpressionSource -> {
                                    val location = location(file, source.origin)
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} of type ${target.type.dumpKotlinLike()} is unsafe to be assigned from expression of platform type ${source.type.removeAnnotations().dumpKotlinLike()}"
                                    Problem.warning(description, location)
                                }
                                is ResolvedSource -> {
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} automatically resolved from ${source.property.dumpKotlinLike()} but it is unsafe to assign source platform type ${source.type.removeAnnotations().dumpKotlinLike()} to target type ${target.type.dumpKotlinLike()}"
                                    Problem.warning(description, null)
                                }
                                is ValueSource -> {
                                    val location = source.origin?.let { location(file, it) }
                                    val description = "Target ${mapping.targetType.dumpKotlinLike()}::${target.name.asString()} of type ${target.type.dumpKotlinLike()} is unsafe to assigned from value of platform type ${source.type.removeAnnotations().dumpKotlinLike()}"
                                    Problem.warning(description, location)
                                }
                            }
                        }
                )

                addAll(
                    mapping.unknowns.map {
                        Problem.error("Parameter ${it.key.asString()} does not occur as a parameter in constructor")
                    }
                )

                with(mapping.symbol.owner) {
                    if (!visibility.isPublicAPI && context.configuration.strictness.visibility) {
                        val constructor = valueParameters.joinToString(prefix = name.asString() + "(", postfix = ")") {
                            it.type.dumpKotlinLike()
                        }
                        add(Problem.error("Constructor $constructor is not visible from the current scope", location(this)))
                    }
                }
            }
    }

    private class EnumMappingValidation(private val mapping: EnumMapping) : MappingValidation {

        override val problems: List<Problem> =
            if (context.configuration.strictness.enums) {
                mapping.mappings
                    .filter { (_, targets) -> targets.size != 1 }
                    .map { (source, targets) ->
                        Problem.error("Source ${mapping.sourceType.dumpKotlinLike()}.${source.name.asString()} has ${if (targets.isEmpty()) "no target defined" else "multiple targets defined"}")
                    }
            } else {
                emptyList()
            }
    }

    companion object {
        fun of(file: IrFileEntry, mapping: Mapping): MappingValidation =
            when (mapping) {
                is EnumMapping -> EnumMappingValidation(mapping)
                is ConstructorCallMapping -> ConstructorCallMappingValidation(file, mapping)
                else -> object : MappingValidation { override val problems = emptyList<Problem>() }
            }
    }
}
