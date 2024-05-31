package io.github.stefankoppier.mapping.traversal

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolver.MappingResolver
import io.github.stefankoppier.mapping.util.isSubclassOfFqName
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
class IrTransformer(private val pluginContext: MappingPluginContext): IrElementTransformerVoidWithContext() {

    override fun visitFileNew(declaration: IrFile): IrFile {
        val result = super.visitFileNew(declaration)
        return result
    }

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (!declaration.isSubclassOfFqName("io.github.stefankoppier.mapping.annotations.Mapper")) {
            return declaration
        }

        return super.visitClassNew(declaration)
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.name == Name.identifier("map")) {
            val targetClass = requireNotNull(declaration.returnType.getClass()) {
                "Expected return type of map to be non-null."
            }
            val primaryConstructor = requireNotNull(targetClass.primaryConstructor) {
                "The target type must have a primary constructor."
            }
//
            val sourceParameter = requireNotNull(declaration.valueParameters.firstOrNull())
            val sourceClass = requireNotNull(sourceParameter.type.getClass()) {
                "Expected type of source argument to be non-null."
            }

            val arguments = MappingResolver()
                .resolve(target = declaration.returnType, source = sourceParameter.type)

            declaration.body = with (createScope(declaration)) {
                pluginContext.blockBody(this.scope) {
                    +irReturn(irCallConstructor(primaryConstructor.symbol, emptyList()).apply {
                        arguments.mapIndexed { index, (type, argument) ->
                            putValueArgument(index, irCall(sourceClass.getPropertyGetter(argument.name.asString())!!).apply {
                                dispatchReceiver = irGet(type, sourceParameter.symbol)
                            })
                        }
                    })
                }
            }

        }
        return declaration
    }
}

data class MappingTarget(val name: Name)

sealed class MappingSource<T : IrExpression>(val name: Name, val value: T)

class PropertySource(name: Name, value: IrPropertyReference) : MappingSource<IrPropertyReference>(name, value)

data class FromToGatherings(
    val to: MutableList<MappingTarget> = mutableListOf(),
    val from: MutableList<MappingSource<*>> = mutableListOf(),
)

@OptIn(UnsafeDuringIrConstructionAPI::class)
class IrFromToGatherer(private val context: MappingPluginContext) : IrElementVisitor<FromToGatherings, FromToGatherings> {

    override fun visitBlock(expression: IrBlock, data: FromToGatherings): FromToGatherings {
        require(expression.statements.size == 1)
        return expression.statements.first().accept(this, data)
    }

    override fun visitReturn(expression: IrReturn, data: FromToGatherings): FromToGatherings {
        return expression.value.accept(this, data)
    }

    override fun visitBlockBody(body: IrBlockBody, data: FromToGatherings): FromToGatherings {
        require(body.statements.size == 1)
        return body.statements.first().accept(this, data)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: FromToGatherings): FromToGatherings {
        return expression.function.body!!.accept(this, data)
    }

    override fun visitCall(expression: IrCall, data: FromToGatherings): FromToGatherings {
        when (expression.symbol.owner.name) {
            Name.identifier("mapping") -> {
                return expression.valueArguments.fold(data) { acc, valueArgument ->
                    valueArgument!!.accept(this, acc)
                }
            }
            Name.identifier("mappedTo") -> {
//                data.to.add(expression.extensionReceiver!!.accept(IrTargetGatherer(context), Unit))
//                data.from.add(expression.valueArguments.first()!!.accept(IrSourceGatherer(context), Unit))
            }
            else -> TODO("IrFromToGatherer: Not yet implemented for ${expression.dump()}")
        }
        return data
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: FromToGatherings): FromToGatherings {
        return expression.argument.accept(this, data)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: FromToGatherings): FromToGatherings {
        return data
    }

    override fun visitElement(element: IrElement, data: FromToGatherings): FromToGatherings {
        TODO("IrFromToGatherer: Not yet implemented for ${element::class} ${element.dump()}")
    }
}

class IrTargetGatherer(private val context: MappingPluginContext) : IrElementVisitor<MappingTarget, Unit> {
    override fun visitElement(element: IrElement, data: Unit): MappingTarget {
        TODO("IrTargetGatherer: Not yet implemented for ${element.dump()}")
    }
}

class IrSourceGatherer(private val context: MappingPluginContext) : IrElementVisitor<MappingSource<*>, Unit> {
    override fun visitElement(element: IrElement, data: Unit): MappingSource<*> {
        TODO("IrSourceGatherer: Not yet implemented for ${element.dump()}")
    }
}