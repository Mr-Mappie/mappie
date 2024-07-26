package tech.mappie.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.types.typeWithParameters
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.IDENTIFIER_MAP
import tech.mappie.resolving.classes.GeneratedMappieEnumClass
import tech.mappie.resolving.enums.EnumMappingsConstructor
import tech.mappie.util.referenceEnumMappieClass

class EnumMapperClassGenerator(val parent: IrClass) : IrElementTransformerVoidWithContext() {

    private val mappie = referenceEnumMappieClass()

    fun generate(generated: GeneratedMappieEnumClass): IrClass =
        context.irFactory.buildClass {
            name = generated.name
            kind = ClassKind.OBJECT
        }.apply {
            parent = this@EnumMapperClassGenerator.parent
            thisReceiver = buildReceiverParameter(this, origin, symbol.typeWithParameters(emptyList()))
            superTypes = listOf(mappie.owner.symbol.typeWith(listOf(generated.source, generated.target)))

            addSimpleDelegatingConstructor(
                superConstructor = mappie.constructors.single().owner,
                isPrimary = true,
                irBuiltIns = context.irBuiltIns,
            )

            mappie.functions.forEach { function ->
                addFunction {
                    name = function.owner.name
                    returnType = function.owner.returnType
                    updateFrom(function.owner)
                }.apply {
                    dispatchReceiverParameter = function.owner.dispatchReceiverParameter
                    overriddenSymbols = listOf(function)
                    function.owner.valueParameters.forEach { parameter ->
                        addValueParameter(parameter.name, parameter.type)
                    }
                    if (function.owner.name == IDENTIFIER_MAP) {
                        val mapping = EnumMappingsConstructor.of(generated.target, generated.source).apply {
                            sources.addAll(generated.sourceEntries)
                            targets.addAll(generated.targetEntries)
                        }.construct()

                        body = EnumMappingConstructor(mapping, this).construct(createScope(this).scope)
                    } else {
                        body = function.owner.body
                        isFakeOverride = true
                    }
                }
            }
        }
}