package tech.mappie.ir.resolving.classes.sources

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.addAnnotations
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import tech.mappie.ir.MappieIrRegistrar.Companion.context
import tech.mappie.ir.resolving.MappieDefinition
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.util.isList
import tech.mappie.ir.util.isSet
import tech.mappie.ir.util.mappieType

sealed interface TransformableClassMappingSource : ClassMappingSource {
    val transformation: PropertyMappingTransformation?

    fun selectGeneratedTransformationMapping(): GeneratedViaMapperTransformation? =
        transformation as? GeneratedViaMapperTransformation?

    fun type(original: IrType, transformation: PropertyMappingTransformation?): IrType {
        return if (transformation == null) {
            original
        } else {
            when (transformation) {
                is PropertyMappingViaMapperTransformation, is GeneratedViaMapperTransformation -> {
                    when {
                        original.isSet() -> context.irBuiltIns.setClass.typeWith(transformation.type)
                        original.isList() -> context.irBuiltIns.listClass.typeWith(transformation.type)
                        else -> transformation.type
                    }.run { if (original.isNullable()) makeNullable() else this }.addAnnotations(original.annotations)
                }
                is PropertyMappingTransformTranformation -> {
                    transformation.type
                }
            }
        }
    }
}

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
