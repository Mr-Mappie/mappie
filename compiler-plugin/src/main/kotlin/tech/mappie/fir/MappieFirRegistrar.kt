package tech.mappie.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import tech.mappie.MappieState
import tech.mappie.fir.analysis.MappieAdditionalCheckersExtension
import tech.mappie.fir.resolving.MappingResolverExtension

class MappieFirRegistrar(val state: MappieState) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::MappieAdditionalCheckersExtension
        +{ session: FirSession -> MappingResolverExtension(state, session) }
    }
}