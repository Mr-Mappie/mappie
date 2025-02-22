package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.isEnumClass
import org.jetbrains.kotlin.ir.util.parentAsClass
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.util.location

class EnumConstructionProblems(
    private val context: ValidationContext,
    private val mapping: ClassMappingRequest,
) {

    fun all(): List<Problem> =
        if (mapping.target.type.classOrNull?.owner?.isEnumClass == true) {
            listOf(Problem.error(
                "Target type ${mapping.target.type.classOrFail.owner.name.asString()} cannot be an enum class",
                location(context.function.parentAsClass),
                listOf("Override EnumMappie instead of ObjectMappie")
            ))
        } else {
            listOf()
        }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): EnumConstructionProblems {
            return EnumConstructionProblems(context, mapping)
        }
    }
}