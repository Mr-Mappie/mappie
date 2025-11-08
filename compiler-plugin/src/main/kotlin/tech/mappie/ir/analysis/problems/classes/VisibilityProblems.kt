package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.ir.MappieContext
import tech.mappie.config.options.useStrictVisibility
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem

class VisibilityProblems(private val mapping: ClassMappingRequest) {

    private val constructor = mapping.constructor.symbol.owner

    context (context: MappieContext)
    fun all(): List<Problem> =
        if (!constructor.visibility.isPublicAPI && useStrictVisibility(mapping.origin.referenceMapFunction())) {
            val constructor = constructor.parameters.filter { it.kind == IrParameterKind.Regular }.joinToString(prefix = "${constructor.constructedClass.name.asString()}(", postfix = ")") {
                it.name.asString() + ": " + it.type.dumpKotlinLike()
            }
            listOf(Problem.error("Constructor $constructor is not visible from the current scope", location(mapping.origin.referenceMapFunction())))
        } else {
            emptyList()
        }

    companion object {
        fun of(mapping: ClassMappingRequest): VisibilityProblems =
            VisibilityProblems(mapping)
    }
}