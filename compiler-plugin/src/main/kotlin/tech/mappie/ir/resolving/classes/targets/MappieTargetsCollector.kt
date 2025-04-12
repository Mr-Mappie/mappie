package tech.mappie.ir.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.util.substituteTypeVariable

class MappieTargetsCollector(function: IrFunction?, constructor: IrConstructor) {

    private val type = constructor.returnType

    private val parameters: List<ClassMappingTarget> = run {
        val parameters = constructor.constructedClass.typeParameters
        val arguments = (function?.returnType?.type as? IrSimpleType)?.arguments?.map { it.typeOrFail } ?: emptyList()
        constructor.valueParameters.map {
            ValueParameterTarget(it, it.type.substitute(parameters, arguments))
        }
    }

    private val setters: Sequence<ClassMappingTarget> = run {
        type.classOrFail.owner.properties.mapNotNull { property ->
            property.setter?.let { setter ->
                if (function != null) {
                    property to setter.valueParameters.first().type.substituteTypeVariable(constructor.constructedClass, (function.returnType.type as IrSimpleType).arguments)
                } else {
                    property to type
                }
            }
        }
            .filter { property -> property.first.name !in parameters.map { it.name } }
            .map { SetterTarget(it.first, it.second) }
    }

    // TODO: set methods
    private val setMethods: Sequence<ClassMappingTarget> =
        type.classOrFail.functions
//        (function?.parentAsClass?.functions?.map { it.symbol } ?: type.classOrFail.functions)
            .filter { it.owner.name.asString().startsWith("set") && it.owner.valueParameters.size == 1 }
            .map { FunctionCallTarget(it) }


    fun collect(): List<ClassMappingTarget> = parameters + setters + setMethods
}
