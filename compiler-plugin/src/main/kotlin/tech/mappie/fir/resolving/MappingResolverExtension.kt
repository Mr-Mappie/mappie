package tech.mappie.fir.resolving

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirClass
import tech.mappie.MappieState
import tech.mappie.fir.resolving.classes.ClassMappingResolver
import tech.mappie.fir.resolving.enums.EnumMappingResolver
import tech.mappie.fir.util.isSubclassOfEnumMappie
import tech.mappie.fir.util.isSubclassOfAnObjectMappie

class MappingResolverExtension(private val state: MappieState, session: FirSession)
    : FirAdditionalCheckersExtension(session) {

    override val declarationCheckers = object : DeclarationCheckers() {
        override val classCheckers: Set<FirClassChecker> = setOf(
            object : FirClassChecker(MppCheckerKind.Common) {

                context(context: CheckerContext, reporter: DiagnosticReporter)
                override fun check(declaration: FirClass) {
                    val mapping = when {
                        declaration.symbol.isSubclassOfEnumMappie() ->
                             EnumMappingResolver(context.session).resolve(declaration.symbol)
                        declaration.symbol.isSubclassOfAnObjectMappie() ->
                            ClassMappingResolver(context.session).resolve(declaration.symbol)
                        else ->
                            null
                    }
                    if (mapping != null) {
                        state.models.put(declaration.symbol.classId, mapping)
                    }
                }
            }
        )
    }
}