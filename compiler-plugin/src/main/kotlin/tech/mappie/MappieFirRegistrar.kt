package tech.mappie

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import tech.mappie.analysis.MappieAdditionalCheckersExtension

class MappieFirRegistrar : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +::MappieAdditionalCheckersExtension
    }
}