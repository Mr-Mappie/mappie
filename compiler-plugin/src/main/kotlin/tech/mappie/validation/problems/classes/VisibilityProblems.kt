package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class VisibilityProblems(private val context: ValidationContext, private val owner: IrConstructor) {

    fun all(): List<Problem> =
        if (!owner.visibility.isPublicAPI && context.configuration.strictness.visibility) {
            val constructor = owner.valueParameters.joinToString(prefix = owner.name.asString() + "(", postfix = ")") {
                it.type.dumpKotlinLike()
            }
            listOf(Problem.error("Constructor $constructor is not visible from the current scope", location(owner)))
        } else {
            emptyList()
        }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): VisibilityProblems =
            VisibilityProblems(context, mapping.constructor)
    }
}