package tech.mappie.validation.problems

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.util.location
import tech.mappie.validation.Problem

class UnknownParameterNameProblems(private val unknowns: List<Name>) {

    fun all(): List<Problem> = unknowns.map {
        Problem.error("Parameter ${it.asString()} does not occur as a parameter in constructor")
    }

    companion object {
        fun of(mapping: ConstructorCallMapping): UnknownParameterNameProblems =
            UnknownParameterNameProblems(mapping.unknowns.map { it.key })
    }
}

class VisibilityProblems(private val owner: IrConstructor) {

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
        fun of(mapping: ConstructorCallMapping): VisibilityProblems =
            VisibilityProblems(mapping.symbol.owner)
    }
}