package tech.mappie.ir.resolving

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.CommonRenderers.STRING
import org.jetbrains.kotlin.psi.KtElement
import kotlin.getValue

object MappieIrResolvingProblems  : KtDiagnosticsContainer() {
    val MAPPIE_MULTIPLE_IMPLICIT_MAPPERS by error1<KtElement, String>(WHOLE_ELEMENT)

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = MappieIrResolvingMessages
}

object MappieIrResolvingMessages : BaseDiagnosticRendererFactory() {
    override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("MappieIrResolvingMessages") { map ->
        map.put(MappieIrResolvingProblems.MAPPIE_MULTIPLE_IMPLICIT_MAPPERS, "More than one mapper resolved which can be called implicitly ''{0}''.", STRING)
    }
}