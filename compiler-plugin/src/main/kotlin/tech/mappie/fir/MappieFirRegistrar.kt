package tech.mappie.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import tech.mappie.fir.analysis.MappieErrors

class MappieFirRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::MappieAdditionalCheckersExtension

        registerDiagnosticContainers(MappieErrors)
    }
}