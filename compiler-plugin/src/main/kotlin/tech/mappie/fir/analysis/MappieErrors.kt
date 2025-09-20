package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement

object MappieErrors : KtDiagnosticsContainer() {
    val MULTIPLE_MAPPING_CALLS by error0<KtElement>(WHOLE_ELEMENT)
    val INVALID_ANONYMOUS_OBJECT by error0<KtElement>(WHOLE_ELEMENT)
    val COMPILE_TIME_RECEIVER by error1<KtElement, Name>(WHOLE_ELEMENT)
    val COMPILE_TIME_EXTENSION_RECEIVER by error1<KtElement, Name>(WHOLE_ELEMENT)
    val NON_CONSTANT_ERROR by error0<KtElement>(WHOLE_ELEMENT)
    val UNKNOWN_NAME_ERROR by error1<KtElement, String>(WHOLE_ELEMENT)
    val ANNOTATION_USE_DEFAULT_ARGUMENTS_NOT_APPLICABLE by warning0<KtElement>(WHOLE_ELEMENT)
    val ANNOTATION_USE_STRICT_ENUMS_NOT_APPLICABLE by warning0<KtElement>(WHOLE_ELEMENT)
    val ANNOTATION_USE_STRICT_VISIBILITY_NOT_APPLICABLE by warning0<KtElement>(WHOLE_ELEMENT)
    val UNNECESSARY_EXPLICIT_MAPPING by warning1<KtElement, String>(WHOLE_ELEMENT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = DefaultErrorMessageMappie
}

object DefaultErrorMessageMappie : BaseDiagnosticRendererFactory() {
    override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("Mappie") { map ->
        map.put(MappieErrors.MULTIPLE_MAPPING_CALLS, "Multiple calls of the function 'mapping' while only one is allowed")
        map.put(MappieErrors.INVALID_ANONYMOUS_OBJECT, "Anonymous Mappie objects are not supported")
        map.put(MappieErrors.COMPILE_TIME_RECEIVER, "The function ''{0}'' was called on the mapping dsl which does not exist after compilation", CommonRenderers.NAME)
        map.put(MappieErrors.COMPILE_TIME_EXTENSION_RECEIVER, "The function ''{0}'' was called as an extension method on the mapping dsl which does not exist after compilation", CommonRenderers.NAME)
        map.put(MappieErrors.NON_CONSTANT_ERROR, "Argument must be a compile-time constant")
        map.put(MappieErrors.UNKNOWN_NAME_ERROR, "Identifier ''{0}'' does not occur as as setter or as a parameter in constructor", CommonRenderers.STRING)
        map.put(MappieErrors.ANNOTATION_USE_DEFAULT_ARGUMENTS_NOT_APPLICABLE, "Annotation @UseDefaultArguments has no effect on subclass of EnumMappie")
        map.put(MappieErrors.ANNOTATION_USE_STRICT_ENUMS_NOT_APPLICABLE, "Annotation @UseStrictEnums has no effect on subclass of ObjectMappie")
        map.put(MappieErrors.ANNOTATION_USE_STRICT_VISIBILITY_NOT_APPLICABLE, "Annotation @UseStrictVisibility has no effect on subclass of EnumMappie")
        map.put(MappieErrors.UNNECESSARY_EXPLICIT_MAPPING, "Unnecessary explicit mapping of source ''{0}''", CommonRenderers.STRING)
    }
}