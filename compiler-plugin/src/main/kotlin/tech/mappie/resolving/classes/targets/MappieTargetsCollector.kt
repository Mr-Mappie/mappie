package tech.mappie.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.properties

class MappieTargetsCollector(val constructor: IrConstructor) {

    private val type = constructor.returnType

    private val parameters: List<MappieTarget> =
        constructor.valueParameters.map { MappieValueParameterTarget(it) }

    private val setters: Sequence<MappieTarget> =
        type.classOrFail.owner.properties
            .filter { property -> property.setter != null && property.name !in parameters.map { it.name } }
            .map { MappieSetterTarget(it) }

    fun all(): List<MappieTarget> =
        parameters + setters // TODO: + setX functions
}
