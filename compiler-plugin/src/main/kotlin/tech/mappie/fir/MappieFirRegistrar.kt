package tech.mappie.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class MappieFirRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::MappieAdditionalCheckersExtension
    }
}