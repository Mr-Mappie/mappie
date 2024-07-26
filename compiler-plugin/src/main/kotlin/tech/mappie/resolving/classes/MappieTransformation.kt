package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.resolving.MappieDefinition

sealed interface MappieTransformation {
    val type: IrType
}

data class MappieTransformOperator(
    val function: IrFunctionExpression,
) : MappieTransformation {
    override val type: IrType = function.type
}

data class MappieViaOperator(
    val definition: MappieDefinition,
    val dispatchReceiver: IrExpression,
) : MappieTransformation {
    override val type = definition.toType
}

data class MappieViaResolved(
    val definition: MappieDefinition,
) : MappieTransformation {
    override val type = definition.toType
}

data class MappieViaGeneratedClass(
    val definition: GeneratedMappieClass,
) : MappieTransformation {
    override val type = definition.target
}

