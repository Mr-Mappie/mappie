package tech.mappie.ir.generation.enums

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.superClass
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.EnumMappieCodeGenerationModel
import tech.mappie.ir.generation.MappieCodeGenerator
import tech.mappie.ir.resolving.SuperCallEnumMappings
import tech.mappie.ir.util.isMappieMapFunction

class SuperCallEnumMappieCodeGenerator(
    override val model: EnumMappieCodeGenerationModel
) : MappieCodeGenerator(model) {

    init {
        require(model.mappings is SuperCallEnumMappings)
    }

    context(context: MappieContext)
    override fun IrBlockBodyBuilder.content() {
        val parent = model.definition.clazz.superClass!!
        +irReturn(irCall(parent.functions.first { it.isMappieMapFunction() }, null, parent.symbol).apply {
            model.definition.referenceMapFunction().parameters.forEach { parameter ->
                arguments[parameter.indexInParameters] = irGet(parameter)
            }
        })
    }
}