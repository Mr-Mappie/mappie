package tech.mappie.ir.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.addAnnotations
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.isNullable
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget

sealed interface TransformableClassMappingSource : ClassMappingSource {
    val source: IrType
    val transformation: PropertyMappingTransformation?

    fun type(original: IrType): IrType =
        when (transformation) {
            is PropertyMappingViaMapperTransformation, is GeneratedViaMapperTransformation -> {
                if (original.isNullable()) {
                    transformation!!.type.makeNullable().addAnnotations(original.annotations)
                } else {
                    transformation!!.type
                }
            }
            else -> {
                transformation?.type ?: original.type
            }
        }
}

sealed interface PropertyMappingTransformation {
    val type: IrType
}

@ConsistentCopyVisibility
data class PropertyMappingTransformTransformation private constructor(
    val function: IrExpression,
    override val type: IrType,
) : PropertyMappingTransformation {
    constructor(functionReference: IrFunctionReference) : this(functionReference, functionReference.symbol.owner.returnType)
    constructor(functionExpression: IrFunctionExpression) : this(functionExpression, functionExpression.function.returnType)
}

data class PropertyMappingViaMapperTransformation(
    val mapper: MappieDefinition,
    val dispatchReceiver: IrExpression?,
    val target: IrType
) : PropertyMappingTransformation {
    override val type = target
}

data class GeneratedViaMapperTransformation(
    val source: ClassMappingSource,
    val target: ClassMappingTarget,
    val lookupScope: IrClass?
) : PropertyMappingTransformation {
    override val type = target.type
}
