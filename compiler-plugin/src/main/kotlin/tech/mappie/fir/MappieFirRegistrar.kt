package tech.mappie.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import tech.mappie.fir.analysis.MappieFirProblems

class MappieFirRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::MappieAdditionalCheckersExtension

        registerDiagnosticContainers(MappieFirProblems)
    }
}