package tech.mappie.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties

class MappieTargetsCollector(val constructor: IrConstructor) {

    private val type = constructor.returnType

    private val parameters: List<MappieTarget> =
        constructor.valueParameters.map { MappieValueParameterTarget(it) }

    private val setters: Sequence<MappieTarget> =
        type.classOrFail.owner.properties
            .filter { property -> property.setter != null && property.name !in parameters.map { it.name } }
            .map { MappieSetterTarget(it) }

    private val setMethods: Sequence<MappieTarget> =
        type.classOrFail.functions
            .filter { it.owner.name.asString().startsWith("set") && it.owner.valueParameters.size == 1 }
            .map { MappieFunctionTarget(it) }


    fun all(): List<MappieTarget> =
        parameters + setters + setMethods
}
