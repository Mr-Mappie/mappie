package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.types.*
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.util.isList
import tech.mappie.util.isSet

sealed interface ClassMappingSource {
    val type: IrType

    fun selectGeneratedTransformationMapping(): GeneratedViaMapperTransformation? = null
}

internal fun type(original: IrType, transformation: PropertyMappingTransformation?): IrType {
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
