package tech.mappie.ir.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.util.constructedClass
import tech.mappie.ir.util.arguments
import tech.mappie.ir.util.substituteTypeVariable

class MappieTargetsCollector(
    private val target: IrType,
    private val function: IrFunction?,
    private val constructor: IrConstructor
) {

    private val parameters: List<ClassMappingTarget> = run {
        val parameters = constructor.constructedClass.typeParameters
        val arguments = function?.returnType?.arguments?.map { it.typeOrFail } ?: emptyList()
        constructor.parameters.filter { it.kind == IrParameterKind.Regular }.map {
            ValueParameterTarget(it, it.type.substitute(parameters, arguments))
        }
    }

    private val setters: Sequence<ClassMappingTarget> = run {
        target.classOrFail.owner.properties.mapNotNull { property ->
            property.setter?.let { setter ->
                property to setter.parameters.first { it.kind == IrParameterKind.Regular }.type.substituted()
            }
        }
            .filter { property -> property.first.name !in parameters.map { it.name } }
            .map { SetterTarget(it.first, it.second) }
    }

    private val setMethods: Sequence<ClassMappingTarget> =
        target.classOrFail.functions
            .filter { it.owner.name.asString().startsWith("set") && it.owner.parameters.count { it.kind == IrParameterKind.Regular } == 1 }
            .map {
                FunctionCallTarget(
                    it,
                    it.owner.parameters.first { it.kind == IrParameterKind.Regular }.type.substituted()
                )
            }

    private fun IrType.substituted(): IrType =
        if (this is IrSimpleType) {
            substituteTypeVariable(constructor.constructedClass, (target as IrSimpleType).arguments)
        } else {
            type
        }

    fun collect(): List<ClassMappingTarget> = parameters + setters + setMethods
}
