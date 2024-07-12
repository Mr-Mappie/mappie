package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name
import tech.mappie.util.dumpKotlinLike
import tech.mappie.util.getterName

sealed interface MappieSource {
    val name: Name
    val type: IrType
    val function: IrFunction
    val holder: IrValueParameter

    fun dumpKotlinLike(): String
}

data class MappiePropertySource(override val function: IrSimpleFunction, override val holder: IrValueParameter) :
    MappieSource {
    override val name = function.name
    override val type = function.returnType

    override fun dumpKotlinLike(): String = function.symbol.dumpKotlinLike()
}

data class MappieFunctionSource(override val function: IrFunction, override val holder: IrValueParameter) :
    MappieSource {
    override val name = getterName(function.name.asString().removePrefix("get").replaceFirstChar { it.lowercaseChar() })
    override val type = function.returnType

    override fun dumpKotlinLike(): String = function.name.asString()
}
