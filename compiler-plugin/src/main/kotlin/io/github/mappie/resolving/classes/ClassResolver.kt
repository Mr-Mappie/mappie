package io.github.mappie.resolving.classes

import io.github.mappie.resolving.*
import io.github.mappie.util.getterName
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.hasDefaultValue
import org.jetbrains.kotlin.ir.util.isClass

class ClassResolver(private val declaration: IrFunction) {

    private val sourceParameter = declaration.valueParameters.first()

    init {
        require(declaration.returnType.getClass()!!.isClass)
    }

    fun resolve(): List<Mapping> {
        val possibilities = declaration.accept(ConstructorsCollector(), Unit)
        val getters = sourceParameter.accept(GettersCollector(), Unit)
        val dispatchReceiverSymbol = sourceParameter.symbol
        val concreteSources = declaration.body?.accept(ObjectBodyCollector(declaration.fileEntry, dispatchReceiverSymbol), Unit) ?: emptyList()

        return possibilities.map { constructor ->
            val targets = constructor.valueParameters
            val mappings = targets.associateWith { target ->
                val concreteSource = concreteSources.firstOrNull { it.first == target.name }

                if (concreteSource != null) {
                    listOf(concreteSource.second)
                } else {
                    val getter = getters.firstOrNull { getter ->
                        getter.name == getterName(target.name)
                    }
                    if (getter != null) {
                        listOf(PropertySource(getter.symbol, target.type, sourceParameter.symbol, true))
                    } else if (target.hasDefaultValue()) {
                        listOf(DefaultParameterValueSource(target.defaultValue!!.expression))
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