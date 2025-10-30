package tech.mappie.ir.resolving

import org.jetbrains.kotlin.backend.jvm.ir.upperBound
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parents
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.IrLazyGeneratedClass
import tech.mappie.ir.util.*

class MappieDefinitionCollection(
    val internal: MutableList<InternalMappieDefinition> = mutableListOf(),
    val external: MutableList<ExternalMappieDefinition> = mutableListOf(),
    val generated: MutableList<GeneratedMappieDefinition> = mutableListOf(),
) {
    val definitions: Sequence<MappieDefinition>
        get() = internal.asSequence() + generated.asSequence() + external.asSequence()

    fun load(other: MappieDefinitionCollection) {
        internal.addAll(other.internal)
        external.addAll(other.external)
        generated.addAll(other.generated)
    }

    context (context: MappieContext)
    fun matching(source: IrType, target: IrType, parent: IrClass? = null) =
        definitions.filter { mappie ->
            val isSubtype = source.upperBound.isSubtypeOf(mappie.source.upperBound) && mappie.target.upperBound.isSubtypeOf(target.upperBound)
            val isCorrectParent = parent?.let { mappie.clazz !is IrLazyGeneratedClass && (it == mappie.clazz || it in mappie.clazz.parents) } ?: true
            isSubtype && isCorrectParent
        }
}

interface MappieDefinition {
    val clazz: IrClass
    val source: IrType
    val target: IrType

    fun referenceMapFunction() = clazz.functions.first { it.isMappieMapFunction() }
    fun referenceMapNullableFunction() = clazz.functions.first { it.isMappieMapNullableFunction() }
}

data class InternalMappieDefinition(
    override val clazz: IrClass,
    override val source: IrType,
    override val target: IrType,
) : MappieDefinition {

    override fun toString() = "${clazz.name} ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}"

    companion object {
        context (context: MappieContext)
        fun of(clazz: IrClass): InternalMappieDefinition {
            val (source, target) = clazz.mappieSuperClassTypes()
            return InternalMappieDefinition(clazz, source, target)
        }
    }
}

// TODO: also via of companion object function.
class ExternalMappieDefinition(override val clazz: IrClass) : MappieDefinition {
    override val source: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[0].typeOrFail
    override val target: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[1].typeOrFail
}

data class GeneratedMappieDefinition(
    override val clazz: IrClass,
    override val source: IrType,
    override val target: IrType
) : MappieDefinition
