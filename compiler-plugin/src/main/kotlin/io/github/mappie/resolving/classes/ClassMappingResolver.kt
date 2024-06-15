package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.resolving.*
import io.github.mappie.util.irGet
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ClassMappingResolver : BaseVisitor<List<Mapping>, Unit>() {

    override fun visitFunction(declaration: IrFunction, data: Unit): List<Mapping> {
        check(declaration.returnType.getClass()!!.isData)

        val possibilities = declaration.accept(ConstructorsCollector(), Unit)
        val sourceParameter = declaration.valueParameters.first()
        val getters = sourceParameter.accept(GettersCollector(), Unit)
        val dispatchReceiverSymbol = sourceParameter.symbol
        val concreteSources = declaration.body?.accept(ObjectSourcesCollector(declaration.fileEntry, dispatchReceiverSymbol), Unit) ?: emptyList()

        return possibilities.map { constructor ->
            val targets = constructor.valueParameters
            val mappings = targets.associateWith { target ->
                val concreteSource = concreteSources.firstOrNull { it.first == target.name }

                if (concreteSource != null) {
                    listOf(concreteSource.second)
                } else {
                    val getter = getters.firstOrNull { getter ->
                        getter.name == Name.special("<get-${target.name.asString()}>")
                    }
                    if (getter != null) {
                        listOf(PropertySource(getter.symbol, target.type, sourceParameter.symbol))
                    } else {
                        emptyList()
                    }
                }
            }

            ConstructorCallMapping(
                targetType = declaration.returnType,
                sourceType = sourceParameter.type,
                symbol = constructor.symbol,
                mappings = mappings
            )
        }
    }
}