package tech.mappie.fir.resolving.enums

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClassCopy
import org.jetbrains.kotlin.fir.declarations.collectEnumEntries
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.utils.isEnumClass
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.toResolvedEnumEntrySymbol
import org.jetbrains.kotlin.fir.resolve.calls.FirSimpleSyntheticPropertySymbol
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.impl.FirImplicitTypeRefImplWithoutSource
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.fir.visitors.FirTransformer
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import tech.mappie.fir.resolving.EnumMapping
import tech.mappie.util.IDENTIFIER_FROM_ENUM_ENTRY
import tech.mappie.util.IDENTIFIER_MAP
import tech.mappie.util.IDENTIFIER_MAPPING
import tech.mappie.util.IDENTIFIER_THROWN_BY_ENUM_ENTRY

class EnumMappingResolver(private val session: FirSession) {

    @OptIn(SymbolInternals::class)
    fun resolve(clazz: FirClassSymbol<*>): EnumMapping? {
        val superType = clazz.resolvedSuperTypes.first() // TODO: assumes direct inheritance
        val (source, target) = superType.typeArguments.take(2).map { it.type!!.toClassSymbol(session)!! }

        val explicit = clazz.fir.accept(ExplicitEnumMappingCollector(), Unit)
        val targets = if (target.isEnumClass) target.fir.collectEnumEntries(session) else emptyList()

        val mappings: Map<EnumMappingTarget?, FirEnumEntry> = source.fir.collectEnumEntries(session).associateBy { source ->
            explicit[source] ?: ResolvedEnumMappingTarget(targets.first { it.name == source.name })
        }

        clazz.fir.transform<FirClass, Unit>(PropertiesAdder(mappings), Unit)

        return EnumMapping(mappings)
    }
}

class PropertiesAdder(val mappings: Map<EnumMappingTarget?, FirEnumEntry>) : FirTransformer<Unit>() {
    override fun <E : FirElement> transformElement(element: E, data: Unit): E {
        error("Not implemented for ${element::class}")
    }

    override fun transformRegularClass(regularClass: FirRegularClass, data: Unit): FirStatement {
        return buildRegularClassCopy(regularClass) {
            symbol = regularClass.symbol
            mappings.forEach { (target, source) ->
                if (target is ThrownByEnumMappingTarget) {
                    val property = buildProperty {
                        moduleData = regularClass.moduleData
                        status = FirResolvedDeclarationStatusImpl(
                            Visibilities.Private,
                            Modality.FINAL,
                            EffectiveVisibility.PrivateInClass
                        )
                        name = Name.identifier("_mappie-${source.name}")
                        isVar = false
                        isLocal = false
                        symbol = FirSimpleSyntheticPropertySymbol(
                            CallableId(Name.identifier("_mappie-${source.name}")),
                            CallableId(Name.identifier("_mappie-${source.name}"))
                        )
                        returnTypeRef = FirImplicitTypeRefImplWithoutSource
                        initializer = target.expression
                        origin = FirDeclarationOrigin.Plugin(MappieGeneratedDeclarationKey)
                    }
                    declarations.add(property)
                }
            }
        }
    }
}

object MappieGeneratedDeclarationKey : GeneratedDeclarationKey()


@OptIn(DirectDeclarationsAccess::class, SymbolInternals::class)
class ExplicitEnumMappingCollector : FirVisitor<Map<FirEnumEntry, EnumMappingTarget>, Unit>() {

    override fun visitElement(element: FirElement, data: Unit): Map<FirEnumEntry, EnumMappingTarget> {
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
            IDENTIFIER_THROWN_BY_ENUM_ENTRY -> {
                val target = ThrownByEnumMappingTarget(functionCall.extensionReceiver!!)
                val source = (functionCall.arguments.first() as FirPropertyAccessExpression).calleeReference.toResolvedEnumEntrySymbol()!!.fir
                mapOf<FirEnumEntry, EnumMappingTarget>(source to target)
            }
            IDENTIFIER_FROM_ENUM_ENTRY -> {
                TODO("visitFunctionCall $IDENTIFIER_FROM_ENUM_ENTRY")
            }
            else -> {
                emptyMap()
            }
        }

    private fun FirElement.accept() =
        accept(this@ExplicitEnumMappingCollector, Unit)

}