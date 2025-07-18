package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.declarations.FirConstructor

class TargetsCollector {
    fun collect(constructor: FirConstructor): List<ClassMappingTarget> {
        val parameters = constructor.valueParameters.map {
            ValueParameterTarget(it.symbol)
        }

        return parameters
    }
}