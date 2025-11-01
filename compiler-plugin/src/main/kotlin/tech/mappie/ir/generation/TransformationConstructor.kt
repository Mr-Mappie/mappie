package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.superClass
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.analysis.Problem.Companion.internal
import tech.mappie.ir.referenceFunctionError
import tech.mappie.ir.referenceFunctionLet
import tech.mappie.ir.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingTransformTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.util.arguments
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.ir.util.isMappieMapNullableFunction
import tech.mappie.util.CLASS_ID_ANNOTATION_MAPPIE_RESOLVED

context(context: MappieContext)
fun IrBuilderWithScope.constructTransformation(
    origin: InternalMappieDefinition,
    transformation: PropertyMappingTransformation,
    source: IrExpression,
    target: ClassMappingTarget,
): IrExpression =
    when (transformation) {
        is PropertyMappingTransformTransformation -> {
            irCall(referenceFunctionLet()).apply {
                arguments[0] = source
                arguments[1] = transformation.function
            }
        }

        is PropertyMappingViaMapperTransformation -> {
            irCall(transformation.selectMappingFunction(source)).apply {
                arguments[0] = transformation.dispatchReceiver ?: instance(origin, source, target, transformation.mapper.clazz)
                arguments[1] = source
            }
        }

        is GeneratedViaMapperTransformation -> {
            val definitions = context.definitions.matching(
                transformation.source.type,
                transformation.target.type,
                transformation.lookupScope
            ).toList()

            if (definitions.size == 1) {
                val definition = definitions.first().clazz

                if (definition is IrMappieGeneratedClass) {
                    context.logger.log(internal("failed to reference generated mapper ${definition.name}."))

                    irCall(referenceFunctionError()).apply {
                        arguments[0] = IrConstImpl.string(
                            SYNTHETIC_OFFSET,
                            SYNTHETIC_OFFSET,
                            context.pluginContext.irBuiltIns.stringType,
                            "Failed to reference generated mapper ${definition.name}"
                        )
                    }
                } else {
                    irCall(definition.selectMappingFunction(source)).apply {
                        arguments[0] = instance(origin, source, target, definition)
                        arguments[1] = source
                    }
                }
            } else {
                context.logger.log(internal("failed to reference generated mapper."))

                irCall(referenceFunctionError()).apply {
                    arguments[0] = IrConstImpl.string(
                        SYNTHETIC_OFFSET,
                        SYNTHETIC_OFFSET,
                        context.pluginContext.irBuiltIns.stringType,
                        "Failed to reference generated mapper"
                    )
                }
            }
        }
    }

private fun PropertyMappingViaMapperTransformation.selectMappingFunction(value: IrExpression) =
    when {
        value.type.isNullable() -> mapper.referenceMapNullableFunction()
        else -> mapper.referenceMapFunction()
    }

private fun IrClass.selectMappingFunction(value: IrExpression) =
    when {
        value.type.isNullable() -> listOf(this, superClass!!).firstNotNullOf { it.functions.firstOrNull { it.isMappieMapNullableFunction() } }
        else -> functions.first { it.isMappieMapFunction() }
    }

context(context: MappieContext)
private fun IrBuilderWithScope.instance(origin: InternalMappieDefinition, source: IrExpression, target: ClassMappingTarget, clazz: IrClass): IrDeclarationReference =
    if (clazz is IrMappieGeneratedClass) {
        irCall(referenceFunctionError()).apply {
            arguments[0] = IrConstImpl.string(
                SYNTHETIC_OFFSET,
                SYNTHETIC_OFFSET,
                context.pluginContext.irBuiltIns.stringType,
                "Failed to reference generated mapper"
            )
        }
    } else if (clazz.isObject) {
        irGetObject(clazz.symbol)
    } else if (clazz.primaryConstructor != null && clazz.primaryConstructor!!.parameters.all { it.hasAnnotation(CLASS_ID_ANNOTATION_MAPPIE_RESOLVED) }) {
        irCallConstructor(clazz.primaryConstructor!!.symbol, emptyList()).apply {
            clazz.primaryConstructor!!.parameters.forEach { parameter ->
                val sourceType = source.type.arguments.first().typeOrFail
                val targetType = target.type.arguments.first().typeOrFail

                val inner = context.definitions.matching(sourceType, targetType).single()
                // TODO: should collect inner source and target.
                val instance = instance(origin, source, target, inner.clazz)
                this.arguments[parameter.indexInParameters] = instance
            }
        }
    } else {
        panic("Class ${clazz.name.asString()} should either be an object or have a primary constructor without parameters.", clazz)
    }
