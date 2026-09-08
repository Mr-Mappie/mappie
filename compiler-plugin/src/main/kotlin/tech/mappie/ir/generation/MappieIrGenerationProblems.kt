package tech.mappie.ir.generation

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.errorWithoutSource
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.STRING
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.psi.KtElement
import kotlin.getValue

object MappieIrGenerationProblems  : KtDiagnosticsContainer() {
    val MAPPIE_FAILED_TO_CREATE_REPORT_FILE by error0<KtElement>()
    val MAPPIE_INCOMPREHENSIBLE_REPORT_FILE by warning0<KtElement>()
    val MAPPIE_NO_IMPLICIT_MAPPING by error2<KtElement, String, String>(WHOLE_ELEMENT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = MappieIrGenerationMessages
}

object MappieIrGenerationMessages : BaseDiagnosticRendererFactory() {
    override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("MappieIrGenerationMessages") { map ->
        map.put(MappieIrGenerationProblems.MAPPIE_FAILED_TO_CREATE_REPORT_FILE, "Failed to create report file.")
        map.put(MappieIrGenerationProblems.MAPPIE_INCOMPREHENSIBLE_REPORT_FILE, "Failed to generate comprehensible report.")
        map.put(MappieIrGenerationProblems.MAPPIE_NO_IMPLICIT_MAPPING, "No implicit mapping can be generated from ''{0}'' to ''{1}''.", STRING, STRING)
    }
}