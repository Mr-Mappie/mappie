package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.util.error
import io.github.mappie.util.location
import io.github.mappie.resolving.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.properties

class ClassMappingResolver : BaseVisitor<Mapping, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): Mapping {
        val targetType = declaration.returnType
        check(targetType.getClass()!!.isData)
        val sourceParameter = requireNotNull(declaration.valueParameters.firstOrNull())
        val mappingTarget = declaration.accept(TargetsCollector(), Unit)
        val sourceClass = requireNotNull(sourceParameter.type.getClass()) {
            "Expected type of source argument to be non-null."
        }

        val dispatchReceiverSymbol = declaration.valueParameters.first().symbol
        val concreteSources = declaration.body?.accept(
            ObjectSourcesCollector(dispatchReceiverSymbol, declaration.fileEntry),
            Unit
        ) ?: emptyList()

        return mappingTarget.values.map { target ->
            val concreteSource = concreteSources.firstOrNull { it.first == target.name }

            if (concreteSource != null) {
                concreteSource.second
            } else {
                val source = requireNotNull(sourceClass.properties.firstOrNull { it.name == target.name }) {
                    context.messageCollector.error(
                        "Target ${target.name.asString()} has no source defined",
                        location(declaration)
                    )
                }
                PropertySource(
                    sourceClass.getPropertyGetter(source.name.asString())!!,
                    target.type,
                    sourceParameter.symbol,
                )
            }
        }.let { ConstructorCallMapping(it) }
    }
}