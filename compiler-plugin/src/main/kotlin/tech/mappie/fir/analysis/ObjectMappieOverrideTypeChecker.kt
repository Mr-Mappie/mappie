package tech.mappie.fir.analysis

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAbstractOverrideChecker
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.util.CLASS_ID_OBJECT_MAPPIE

class ObjectMappieOverrideTypeChecker : FirAbstractOverrideChecker(MppCheckerKind.Common) {
    override fun check(declaration: FirClass, context: CheckerContext, reporter: DiagnosticReporter) {
        val base = declaration.superTypeRefs.firstOrNull { it.toRegularClassSymbol(context.session)?.classId == CLASS_ID_OBJECT_MAPPIE }
        if (base != null) {
            val source = base.coneType.typeArguments[0].type?.toClassSymbol(context.session)
            val target = base.coneType.typeArguments[1].type?.toClassSymbol(context.session)

            if (source?.classKind == ClassKind.ENUM_CLASS) {
                reporter.reportOn(declaration.source, ENUM_TYPE, "Source type ${source.name.asString()} cannot be an enum class", context)
            }
            if (target?.classKind == ClassKind.ENUM_CLASS) {
                reporter.reportOn(declaration.source, ENUM_TYPE, "Target type ${target.name.asString()} cannot be an enum class", context)
            }
        }
    }

    companion object {
        private val ENUM_TYPE by error1<KtElement, String>(WHOLE_ELEMENT)
    }
}