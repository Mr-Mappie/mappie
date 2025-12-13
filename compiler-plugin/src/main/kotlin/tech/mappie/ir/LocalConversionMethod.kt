package tech.mappie.ir

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.makeNotNull
import tech.mappie.ir.util.erased
import tech.mappie.ir.util.isSubtypeOf

/**
 * Represents a local conversion method discovered in an ObjectMappie class or its interfaces.
 * These methods take exactly one parameter and return a different type, allowing them to be
 * used automatically for type conversion during mapping.
 *
 * @property function The IR representation of the conversion method
 * @property sourceType The parameter type (input type for conversion)
 * @property targetType The return type (output type for conversion)
 * @property declaringClass The class where this method is declared
 * @property isFromInterface Whether this method comes from an implemented interface
 */
data class LocalConversionMethod(
    val function: IrSimpleFunction,
    val sourceType: IrType,
    val targetType: IrType,
    val declaringClass: IrClass,
    val isFromInterface: Boolean
)

/**
 * Collection of local conversion methods with matching capabilities.
 * Provides methods to find conversion methods that match source and target types.
 */
class LocalConversionMethodCollection(
    val methods: MutableList<LocalConversionMethod> = mutableListOf()
) {
    /**
     * Finds conversion methods that can convert from the given source type to the target type.
     * Methods are matched using subtype relationships to support inheritance hierarchies.
     *
     * @param source The source type to convert from
     * @param target The target type to convert to
     * @return A sequence of matching conversion methods, prioritized by exactness
     */
    context(context: MappieContext)
    fun matching(source: IrType, target: IrType): Sequence<LocalConversionMethod> {
        return methods.asSequence()
            .filter { method ->
                val sourceMatches = source.makeNotNull().isSubtypeOf(method.sourceType.erased(source))
                val targetMatches = method.targetType.erased(target.makeNotNull()).isSubtypeOf(target)
                sourceMatches && targetMatches
            }
            .sortedWith(compareBy(
                // Prefer exact source type match
                { it.sourceType.classOrNull != source.makeNotNull().classOrNull },
                // Prefer exact target type match
                { it.targetType.classOrNull != target.makeNotNull().classOrNull },
                // Prefer methods from the class itself over interface methods
                { it.isFromInterface }
            ))
    }

    fun addAll(other: LocalConversionMethodCollection) {
        methods.addAll(other.methods)
    }

    fun add(method: LocalConversionMethod) {
        methods.add(method)
    }

    fun isEmpty() = methods.isEmpty()

    fun isNotEmpty() = methods.isNotEmpty()
}
