package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.properties

class ClassMappingResolver : BaseVisitor<Mapping, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): Mapping {
        check(declaration.returnType.getClass()!!.isData)

        val sourceParameter = requireNotNull(declaration.valueParameters.firstOrNull())
        val targets = declaration.accept(ValueParametersCollector(), Unit)
        val dispatchReceiverSymbol = declaration.valueParameters.first().symbol
        val concreteSources = declaration.body?.accept(
            ObjectSourcesCollector(dispatchReceiverSymbol, declaration.fileEntry),
            Unit
        ) ?: emptyList()

        val mappings: Map<IrValueParameter, List<MappingSource>> = targets.associateWith { target ->
            val concreteSource = concreteSources.firstOrNull { it.first == target.name }

            if (concreteSource != null) {
                listOf(concreteSource.second)
            } else {
                val sourceClass = requireNotNull(sourceParameter.type.getClass()) {
                    "Expected type of source argument to be non-null."
                }
                val source = sourceClass.properties.firstOrNull { source -> source.name == target.name }
                if (source != null) {
                    val getter = sourceClass.getPropertyGetter(source.name.asString())
                    if (getter != null) {
                        listOf(PropertySource(getter, target.type, sourceParameter.symbol))
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }
        }

        return ConstructorCallMapping(
            targetType = declaration.returnType,
            sourceType = sourceParameter.type,
            mappings = mappings
        )
    }
}