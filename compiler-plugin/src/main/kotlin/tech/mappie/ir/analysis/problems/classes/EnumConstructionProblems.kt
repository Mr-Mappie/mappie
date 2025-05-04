package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.isEnumClass
import org.jetbrains.kotlin.ir.util.parentAsClass
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.ClassRequest
import tech.mappie.ir.resolving.ClassUpdateRequest
import tech.mappie.ir.util.location

class EnumConstructionProblems(
    private val context: ValidationContext,
    private val mapping: ClassRequest,
) {

    fun all(): List<Problem> =
        if (mapping.target.type.classOrNull?.owner?.isEnumClass == true) {
            listOf(Problem.error(
                "Target type ${mapping.target.type.classOrFail.owner.name.asString()} cannot be an enum class",
                location(context.function.parentAsClass),
                when (mapping) {
                    is ClassMappingRequest -> listOf("Override EnumMappie instead of ObjectMappie")
                    is ClassUpdateRequest -> emptyList()
                }

            ))
        } else {
            listOf()
        }

    companion object {
        fun of(context: ValidationContext, mapping: ClassRequest): EnumConstructionProblems {
            return EnumConstructionProblems(context, mapping)
        }
    }
}