package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.resolving.MappieDefinition
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.mappieType

sealed interface PropertyMappingTransformation {
    val type: IrType
}

@ConsistentCopyVisibility
data class PropertyMappingTransformTranformation private constructor(
    val function: IrExpression,
    override val type: IrType,
) : PropertyMappingTransformation {
    constructor(functionReference: IrFunctionReference) : this(functionReference, functionReference.symbol.owner.returnType)
    constructor(functionExpression: IrFunctionExpression) : this(functionExpression, functionExpression.function.returnType)
}

data class PropertyMappingViaMapperTransformation(
    val mapper: MappieDefinition,
    val dispatchReceiver: IrExpression?,
) : PropertyMappingTransformation {
    override val type = mapper.target
}

data class GeneratedViaMapperTransformation(
    val source: ClassMappingSource,
    val target: ClassMappingTarget,
) : PropertyMappingTransformation {
    override val type = target.type.mappieType()
}
