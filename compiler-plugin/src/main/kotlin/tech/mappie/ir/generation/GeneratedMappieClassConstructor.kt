package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.types.typeWithParameters
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import tech.mappie.ir.MappieContext
import tech.mappie.ir.referenceEnumMappieClass
import tech.mappie.ir.referenceObjectMappieClass
import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.util.arguments
import tech.mappie.util.IDENTIFIER_MAP
import java.util.UUID

class GeneratedMappieClassConstructor {

    context (context: MappieContext)
    fun construct(parent: IrClass, definition: GeneratedMappieDefinition, request: CodeGenerationModel): GeneratedMappieDefinition {
        val (container, className) = if (context.pluginContext.platform in JvmPlatforms.allJvmPlatforms) {
            jvmLocation(parent, definition.requestedName)
        } else {
            parent to definition.requestedName
        }

        return context.pluginContext.irFactory.buildClass {
            name = className
            kind = ClassKind.OBJECT
        }.let { clazz ->
            val superClass = when (request) {
                is ClassMappieCodeGenerationModel -> referenceObjectMappieClass()
                is EnumMappieCodeGenerationModel -> referenceEnumMappieClass()
            }

            clazz.configureClass(container, superClass, definition)
            clazz.addDelegatingConstructor(superClass)
            clazz.addFunctionMap(superClass)

            container.addChild(clazz)

            definition.copy(clazz = clazz).also { newDefinition ->
                context.definitions.generated.apply {
                    remove(definition)
                    add(newDefinition)
                }
            }
        }

    }

    private fun jvmLocation(parent: IrClass, name: Name): Pair<IrDeclarationContainer, Name> {
        val owners = generateSequence(parent) { it.parentClassOrNull }.toList().asReversed()
        val binaryName = (owners.map { it.name.asString() } + name.asString()).joinToString("$")
        val binaryNameBytes = binaryName.encodeToByteArray()
        val suffixLength = ".class".length
        val maxFilenameBytes = 255
        if (binaryNameBytes.size + suffixLength <= maxFilenameBytes) {
            return parent to name
        }

        val hash = UUID.nameUUIDFromBytes(binaryNameBytes).toString().replace("-", "")
        val shortName = "Mapper_$hash"

        // A short name alone may not fit when the enclosing generated classes are deeply nested.
        var remaining = maxFilenameBytes - shortName.length - suffixLength
        val container = owners.takeWhile {
            remaining -= it.name.asString().encodeToByteArray().size + 1
            remaining >= 0
        }.lastOrNull() ?: parent.file
        return container to Name.identifier(shortName)
    }

    private fun IrClass.configureClass(parent: IrDeclarationContainer, superClass: IrClassSymbol, definition: GeneratedMappieDefinition) {
        this.parent = parent
        this.thisReceiver = buildReceiverParameter {
            type = symbol.typeWithParameters(emptyList())
        }
        this.superTypes = listOf(superClass.owner.symbol.typeWith(definition.source, definition.target))
    }

    context(context: MappieContext)
    private fun IrClass.addDelegatingConstructor(superClass: IrClassSymbol) {
        val constructor = superClass.constructors.single().owner
        addConstructor {
            isPrimary = true
        }.apply {
            body = context.pluginContext.irBuiltIns.createIrBuilder(symbol).irBlockBody {
                +irDelegatingConstructorCall(constructor)
            }
        }
    }

    private fun IrClass.addFunctionMap(superClass: IrClassSymbol) {
        val function = superClass.functions.single { it.owner.name == IDENTIFIER_MAP }
        addFunction {
            name = function.owner.name
            returnType = superTypes.first().arguments.last().typeOrFail
            updateFrom(function.owner)
        }.apply {
            overriddenSymbols = listOf(function)
            isFakeOverride = false

            parameters += buildReceiverParameter {
                kind = IrParameterKind.DispatchReceiver
                type = function.owner.dispatchReceiverParameter!!.type
            }

            function.owner.parameters.filter { it.kind == IrParameterKind.Regular }.forEach { parameter ->
                addValueParameter {
                    name = parameter.name
                    type = superTypes.first().arguments.first().typeOrFail
                }
            }
        }
    }
}
