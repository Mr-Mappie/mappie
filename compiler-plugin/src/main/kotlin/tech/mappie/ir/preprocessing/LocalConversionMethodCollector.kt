package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.erasedUpperBound
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.LocalConversionMethod
import tech.mappie.ir.LocalConversionMethodCollection
import tech.mappie.ir.MappieContext
import tech.mappie.util.CLASS_ID_EXCLUDE_FROM_MAPPING
import tech.mappie.util.IDENTIFIER_MAP
import tech.mappie.util.IDENTIFIER_MAP_NULLABLE

/**
 * Method names to exclude from local conversion method discovery.
 * These are mappie's own methods that should not be treated as conversion methods.
 */
private val EXCLUDED_METHOD_NAMES: Set<Name> = setOf(
    IDENTIFIER_MAP,
    IDENTIFIER_MAP_NULLABLE,
    Name.identifier("mapList"),
    Name.identifier("mapNullableList"),
    Name.identifier("mapSet"),
    Name.identifier("mapNullableSet"),
    Name.identifier("mapArray"),
    Name.identifier("mapping"),
    Name.identifier("equals"),
)

/**
 * Collects local conversion methods from an ObjectMappie class and its implemented interfaces.
 *
 * Local conversion methods are methods that:
 * - Have exactly one regular parameter
 * - Return a type different from the parameter type
 * - Are not mappie's own methods (map, mapNullable, etc.)
 */
object LocalConversionMethodCollector {

    /**
     * Collects all local conversion methods from the given ObjectMappie class.
     * This includes methods defined in the class itself and default methods from implemented interfaces.
     */
    context (context: MappieContext)
    fun collect(clazz: IrClass): LocalConversionMethodCollection {
        val collection = LocalConversionMethodCollection()

        // Collect methods from the class itself
        clazz.functions
            .filter { it.isConversionMethod() }
            .forEach { function ->
                val param = function.regularParameter()!!
                collection.add(
                    LocalConversionMethod(
                        function = function,
                        sourceType = param.type,
                        targetType = function.returnType,
                        declaringClass = clazz,
                        isFromInterface = false
                    )
                )
            }

        // Collect default methods from implemented interfaces
        clazz.superTypes
            .mapNotNull { it.classOrNull?.owner }
            .filter { it.isInterface }
            .flatMap { collectFromInterface(it) }
            .forEach { method ->
                // Only add if not already overridden in the class
                if (!collection.methods.any {
                    it.function.name == method.function.name &&
                    it.sourceType.classOrNull == method.sourceType.classOrNull
                }) {
                    collection.add(method.copy(declaringClass = clazz, isFromInterface = true))
                }
            }

        return collection
    }

    /**
     * Collects conversion methods from an interface.
     * Only methods with default implementations (body != null) are collected.
     */
    context (context: MappieContext)
    private fun collectFromInterface(interfaceClass: IrClass): List<LocalConversionMethod> {
        val methods = mutableListOf<LocalConversionMethod>()

        interfaceClass.functions
            .filter { it.isConversionMethod() && it.body != null }
            .forEach { function ->
                val param = function.regularParameter()!!
                methods.add(
                    LocalConversionMethod(
                        function = function,
                        sourceType = param.type,
                        targetType = function.returnType,
                        declaringClass = interfaceClass,
                        isFromInterface = true
                    )
                )
            }

        // Also check parent interfaces
        interfaceClass.superTypes
            .mapNotNull { it.classOrNull?.owner }
            .filter { it.isInterface }
            .forEach { parentInterface ->
                methods.addAll(collectFromInterface(parentInterface))
            }

        return methods
    }

    /**
     * Checks if a function is a valid conversion method.
     * A conversion method has exactly one regular parameter and returns a different type.
     */
    context (context: MappieContext)
    private fun IrSimpleFunction.isConversionMethod(): Boolean {
        // Must not be excluded method
        if (name in EXCLUDED_METHOD_NAMES) return false

        // Must not be a fake override (compiler-generated override)
        if (origin == IrDeclarationOrigin.FAKE_OVERRIDE) return false

        // Must not be annotated with @ExcludeFromMapping
        if (hasExcludeFromMappingAnnotation()) return false

        // Must have exactly one regular parameter
        val param = regularParameter() ?: return false

        // Return type must be different from parameter type
        val paramClass = param.type.erasedUpperBound
        val returnClass = returnType.erasedUpperBound
        if (paramClass == returnClass) return false

        return true
    }

    /**
     * Checks if the function has the @ExcludeFromMapping annotation.
     */
    context (context: MappieContext)
    private fun IrSimpleFunction.hasExcludeFromMappingAnnotation(): Boolean {
        val excludeFromMappingSymbol = context.pluginContext.referenceClass(CLASS_ID_EXCLUDE_FROM_MAPPING)
        return annotations.any { it.type.classOrFail == excludeFromMappingSymbol }
    }

    /**
     * Gets the single regular parameter of a function, or null if there isn't exactly one.
     */
    private fun IrSimpleFunction.regularParameter() =
        parameters.singleOrNull { it.kind == IrParameterKind.Regular }
}
