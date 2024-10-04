package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class VisibilityProblems(private val context: ValidationContext, private val owner: IrConstructor) {

    fun all(): List<Problem> =
        if (!owner.visibility.isPublicAPI && context.configuration.strictness.visibility) {
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