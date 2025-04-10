package tech.mappie.ir.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties

class MappieTargetsCollector(constructor: IrConstructor) {

    private val type = constructor.returnType

    // Problem: type of valueParameter is indeed W. This is the type of the constructor parameter.
    // The problem is that the property type is not W, but the instantiated Foo.
    private val parameters: List<ClassMappingTarget> =
        constructor.valueParameters.map { ValueParameterTarget(it) }

    private val setters: Sequence<ClassMappingTarget> =
        type.classOrFail.owner.properties
            .filter { property -> property.setter != null && property.name !in parameters.map { it.name } }
            .map { SetterTarget(it) }

    private val setMethods: Sequence<ClassMappingTarget> =
        type.classOrFail.functions
            .filter { it.owner.name.asString().startsWith("set") && it.owner.valueParameters.size == 1 }
            .map { FunctionCallTarget(it) }


    fun collect(): List<ClassMappingTarget> = parameters + setters + setMethods
}
