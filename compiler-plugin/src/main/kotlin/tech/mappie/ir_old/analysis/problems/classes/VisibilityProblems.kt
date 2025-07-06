package tech.mappie.ir_old.analysis.problems.classes

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.config.options.useStrictVisibility
import tech.mappie.ir_old.resolving.ClassMappingRequest
import tech.mappie.ir_old.util.location
import tech.mappie.ir_old.analysis.Problem
import tech.mappie.ir_old.analysis.ValidationContext

class VisibilityProblems(private val context: ValidationContext, private val owner: IrConstructor) {

    fun all(): List<Problem> =
        if (!owner.visibility.isPublicAPI && context.useStrictVisibility(context.function)) {
            val constructor = owner.parameters.filter { it.kind == IrParameterKind.Regular }.joinToString(prefix = "${owner.constructedClass.name.asString()}(", postfix = ")") {
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