package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.functions
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
import tech.mappie.ir.referenceMappieClass
import tech.mappie.ir.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingTransformTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingViaLocalMethodTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyReferenceMappingTransformTransformation
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.util.allSuperTypes
import tech.mappie.ir.util.arguments
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.ir.util.isMappieMapNullableFunction

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

        is PropertyReferenceMappingTransformTransformation -> {
            irCall(transformation.property.getter!!).apply {
                arguments[0] = source
            }
        }

        is PropertyMappingViaLocalMethodTransformation -> {
            irCall(transformation.method.function.symbol).apply {
                arguments[0] = irGet(origin.referenceMapFunction().dispatchReceiverParameter!!)
                arguments[1] = source
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
                origin,
                transformation.source.type,
                transformation.target.type
            ).toList()

            if (definitions.size == 1) {
                val definition = definitions.first().clazz

                if (definition is IrMappieGeneratedClass) {
                    irCall(referenceFunctionError()).apply {
                        arguments[0] = IrConstImpl.string(
                            SYNTHETIC_OFFSET,
                            SYNTHETIC_OFFSET,
                            context.pluginContext.irBuiltIns.stringType,
                            "Failed to reference generated mapper ${definition.name} in ${origin.clazz.name}"
                        )
                    }
                } else {
                    irCall(definition.selectMappingFunction(source)).apply {
                        arguments[0] = instance(origin, source, target, definition)
                        arguments[1] = source
                    }
                }
            } else {
                if (definitions.isNotEmpty()) {
                    context.logger.log(internal("failed to reference an unique generated mapper in ${origin.clazz.name}. Possible options: " + definitions.joinToString(prefix = "'", postfix = "'") { it.clazz.name.asString() }))
                }

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
    mapper.clazz.selectMappingFunction(value)

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
    } else if (clazz.primaryConstructor != null && clazz.primaryConstructor!!.parameters.all { it.type.classOrNull?.owner?.allSuperTypes()?.any { it.classOrNull == referenceMappieClass() } ?: false }) {
        irCallConstructor(clazz.primaryConstructor!!.symbol, emptyList()).apply {
            clazz.primaryConstructor!!.parameters.forEach { parameter ->
                val sourceType = runCatching { source.type.arguments.first().typeOrNull ?: context.pluginContext.irBuiltIns.anyType.makeNullable() }
                    .getOrElse { panic("Failed to determine type of source argument for parameter ${parameter.name} in ${clazz.name} originating in ${origin.clazz.name}.", cause = it) }
                val targetType = runCatching { target.type.arguments.first().typeOrFail }
                    .getOrElse { panic("Failed determine type of target argument for parameter ${parameter.name} in ${clazz.name} originating in ${origin.clazz.name}.", cause = it) }

                val inner = context.definitions.matching(origin, sourceType, targetType).singleOrNull()
                // TODO: should collect inner source and target.
                val instance = if (inner != null) {
                    instance(origin, source, target, inner.clazz)
                } else {
                    // No mapper exists, as generating one failed. An error has already been reported.
                    irCall(referenceFunctionError()).apply {
                        arguments[0] = IrConstImpl.string(
                            SYNTHETIC_OFFSET,
                            SYNTHETIC_OFFSET,
                            context.pluginContext.irBuiltIns.stringType,
                            "Failed to reference generated mapper"
                        )
                    }
                }
                this.arguments[parameter.indexInParameters] = instance
            }
        }
    } else {
        panic("Class ${clazz.name.asString()} should either be an object or have a primary constructor without parameters.", clazz)
    }
