package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirCallableReferenceAccess
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.toResolvedPropertySymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import tech.mappie.util.IDENTIFIER_FROM_PROPERTY
import tech.mappie.util.IDENTIFIER_MAP
import tech.mappie.util.IDENTIFIER_MAPPING

@OptIn(DirectDeclarationsAccess::class)
class ExplicitClassMappingCollector(private val session: FirSession)
    : FirVisitor<Map<ClassMappingTarget, ClassMappingSource>, Unit>() {

    override fun visitElement(element: FirElement, data: Unit): Map<ClassMappingTarget, ClassMappingSource> {
        error("Not implemented for ${element::class}")
    }

    override fun visitRegularClass(regularClass: FirRegularClass, data: Unit) =
        regularClass.declarations
            .filterIsInstance<FirFunction>()
            .singleOrNull { it.symbol.name == IDENTIFIER_MAP }
            ?.accept() ?: emptyMap()

    override fun visitSimpleFunction(simpleFunction: FirSimpleFunction, data: Unit) =
        simpleFunction.body?.accept() ?: emptyMap()

    override fun visitBlock(block: FirBlock, data: Unit) =
        block.statements.flatMap { it.accept().toList() }.toMap()

    override fun visitReturnExpression(returnExpression: FirReturnExpression, data: Unit) =
        returnExpression.result.accept()

    override fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression, data: Unit) =
        anonymousFunctionExpression.anonymousFunction.body?.accept() ?: emptyMap()

    override fun visitFunctionCall(functionCall: FirFunctionCall, data: Unit) =
        when (functionCall.calleeReference.name) {
            IDENTIFIER_MAPPING -> {
                functionCall.arguments[0].accept()
            }
            IDENTIFIER_FROM_PROPERTY -> {
                val target = functionCall.extensionReceiver!!.accept(ExplicitTargetCollector(session), Unit)
                val source = functionCall.arguments[0].accept(ExplicitSourceCollector(session), Unit)
                mapOf(target to source)
            }
            else -> {
                emptyMap()
            }
        }

    private fun FirElement.accept() =
        accept(this@ExplicitClassMappingCollector, Unit)
}

class ExplicitSourceCollector(private val session: FirSession) : FirVisitor<ClassMappingSource, Unit>() {
    override fun visitElement(element: FirElement, data: Unit): ClassMappingSource {
        error("Unexpected element ${element::class}")
    }

    override fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess, data: Unit): ClassMappingSource {
        return ExplicitPropertySource(callableReferenceAccess)
    }
}

class ExplicitTargetCollector(private val session: FirSession) : FirVisitor<ClassMappingTarget, Unit>() {

    override fun visitElement(element: FirElement, data: Unit): ClassMappingTarget {
        error("Unexpected element ${element::class}")
    }

    @OptIn(SymbolInternals::class)
    override fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess, data: Unit): ClassMappingTarget {
        val calleeReference = callableReferenceAccess.calleeReference
        return NamedValueParameterTarget(
            calleeReference.name,
            // TODO probably needs pattern matching for other kinds.
            calleeReference.toResolvedPropertySymbol()!!.fir.getter!!.returnTypeRef.coneType
        )
    }
}