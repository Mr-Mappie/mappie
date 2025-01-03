package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.config.options.useStrictVisibility
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext

class VisibilityProblems(private val context: ValidationContext, private val owner: IrConstructor) {

    fun all(): List<Problem> =
        if (!owner.visibility.isPublicAPI && context.useStrictVisibility(context.function)) {
            val constructor = owner.valueParameters.joinToString(prefix = "${owner.constructedClass.name.asString()}(", postfix = ")") {
                it.name.asString() + ": " + it.type.dumpKotlinLike()
            }
            listOf(Problem.error("Constructor $constructor is not visible from the current scope", location(context.function)))
        } else {
            emptyList()
        }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): VisibilityProblems =
            VisibilityProblems(context, mapping.constructor)
    }
}