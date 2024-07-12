package tech.mappie.validation.problems

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.MappieIrRegistrar
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.util.location
import tech.mappie.validation.Problem

class VisibilityProblems(private val owner: IrConstructor) {

    fun all(): List<Problem> =
        if (!owner.visibility.isPublicAPI && MappieIrRegistrar.context.configuration.strictness.visibility) {
            val constructor = owner.valueParameters.joinToString(prefix = owner.name.asString() + "(", postfix = ")") {
                it.type.dumpKotlinLike()
            }
            listOf(Problem.error("Constructor $constructor is not visible from the current scope", location(owner)))
        } else {
            emptyList()
        }

    companion object {
        fun of(mapping: ConstructorCallMapping): VisibilityProblems =
            VisibilityProblems(mapping.symbol.owner)
    }
}