package tech.mappie.ir.analysis

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.STRING
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.diagnostics.warning2
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.util.MappieRenderers.IR_CLASS
import tech.mappie.util.MappieRenderers.IR_TYPE
import tech.mappie.util.MappieRenderers.INDENTED_LINES
import tech.mappie.util.MappieRenderers.QUOTED_STRINGS
import kotlin.getValue

object MappieIrAnalysisProblems  : KtDiagnosticsContainer() {
    val MAPPIE_NO_VISIBLE_CONSTRUCTOR by error1<KtElement, IrClassSymbol>()
    val MAPPIE_SINGLE_TARGET_HAS_MULTIPLE_MAPPING_SOURCES by error2<KtElement, String, List<String>>()
    val MAPPIE_MULTIPLE_TARGETS_HAVE_MULTIPLE_MAPPING_SOURCES by error1<KtElement, List<String>>()
    val MAPPIE_MULTIPLE_MAPPING_TARGETS by error1<KtElement, List<String>>()
    val MAPPIE_NO_MAPPING_SOURCE by error1<KtElement, List<String>>()
    val MAPPIE_NO_MAPPING_TARGET by error1<KtElement, List<String>>()
    val MAPPIE_INCORRECT_TARGET_TYPE by error1<KtElement, IrType>()
    val MAPPIE_UNNECESSARY_SAFE_CALL by warning1<KtElement, IrType>()
    val MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT by warning2<KtElement, String, String>()
    val MAPPIE_UNSAFE_TYPE_ASSIGNMENT by error2<KtElement, String, String>()

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = MappieIrAnalysisMessages
}

object MappieIrAnalysisMessages : BaseDiagnosticRendererFactory() {
    override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("MappieIrAnalysisMessages") { map ->
        map.put(MappieIrAnalysisProblems.MAPPIE_NO_VISIBLE_CONSTRUCTOR, "Target class ''{0}'' has no visible constructor.", IR_CLASS)
        map.put(MappieIrAnalysisProblems.MAPPIE_SINGLE_TARGET_HAS_MULTIPLE_MAPPING_SOURCES, "Target ''{0}'' has multiple sources defined {1}.", STRING, QUOTED_STRINGS)
        map.put(MappieIrAnalysisProblems.MAPPIE_MULTIPLE_TARGETS_HAVE_MULTIPLE_MAPPING_SOURCES, "Multiple targets have multiple sources defined:${System.lineSeparator()}{0}", INDENTED_LINES)
        map.put(MappieIrAnalysisProblems.MAPPIE_MULTIPLE_MAPPING_TARGETS, "Source(s) {0} has/have multiple targets defined.", QUOTED_STRINGS)
        map.put(MappieIrAnalysisProblems.MAPPIE_NO_MAPPING_SOURCE, "Target(s) {0} has/have no source defined.", QUOTED_STRINGS)
        map.put(MappieIrAnalysisProblems.MAPPIE_NO_MAPPING_TARGET, "Source(s) {0} has/have no target defined.", QUOTED_STRINGS)
        map.put(MappieIrAnalysisProblems.MAPPIE_INCORRECT_TARGET_TYPE, "Target type ''{0}'' cannot be an enum class.", IR_TYPE)
        map.put(MappieIrAnalysisProblems.MAPPIE_UNNECESSARY_SAFE_CALL, "Unnecessary call to fromPropertyNotNull for non-null type ''{0}''.", IR_TYPE)
        map.put(MappieIrAnalysisProblems.MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT, "Target {0} is unsafe to assign from {1} due to platform type.", STRING, STRING)
        map.put(MappieIrAnalysisProblems.MAPPIE_UNSAFE_TYPE_ASSIGNMENT, "Target {0} cannot be assigned from {1}.", STRING, STRING)
    }
}