package tech.mappie.ir.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.util.substituteTypeVariable

class MappieTargetsCollector(function: IrFunction?, constructor: IrConstructor) {

    private val type = constructor.returnType

    private val parameters: List<ClassMappingTarget> = run {
        val parameters = constructor.constructedClass.typeParameters
        val arguments = (function?.returnType as? IrSimpleType)?.arguments?.map { it.typeOrFail } ?: emptyList()
        constructor.parameters.filter { it.kind == IrParameterKind.Regular }.map {
            ValueParameterTarget(it, it.type.substitute(parameters, arguments))
        }
    }

    private val setters: Sequence<ClassMappingTarget> = run {
        type.classOrFail.owner.properties.mapNotNull { property ->
            property.setter?.let { setter ->
                if (function != null) {
                    property to setter.parameters.first { it.kind == IrParameterKind.Regular }.type.substituteTypeVariable(constructor.constructedClass, (function.returnType as IrSimpleType).arguments)
                } else {
                    property to type
                }
            }
        }
            .filter { property -> property.first.name !in parameters.map { it.name } }
            .map { SetterTarget(it.first, it.second) }
    }

    private val setMethods: Sequence<ClassMappingTarget> =
        type.classOrFail.functions
            .filter { it.owner.name.asString().startsWith("set") && it.owner.parameters.count { it.kind == IrParameterKind.Regular } == 1 }
            .map {
                FunctionCallTarget(
                    it,
                    it.owner.parameters.first { it.kind == IrParameterKind.Regular }.type.substituteTypeVariable(constructor.constructedClass, (function?.returnType as? IrSimpleType)?.arguments!!)
                )
            }


    fun collect(): List<ClassMappingTarget> = parameters + setters + setMethods
}
