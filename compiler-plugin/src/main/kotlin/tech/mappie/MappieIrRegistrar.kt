package tech.mappie

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.generation.*
import tech.mappie.resolving.AllMappieDefinitionsCollector
import tech.mappie.resolving.MappingResolver
import tech.mappie.util.location
import tech.mappie.util.logAll
import tech.mappie.util.logError
import tech.mappie.validation.MappingValidation

class MappieIrRegistrar(
    private val messageCollector: MessageCollector,
    private val configuration: MappieConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        context = MappiePluginContext(messageCollector, configuration, pluginContext)
        val symbols = moduleFragment.accept(AllMappieDefinitionsCollector(), Unit)
        val mappings = moduleFragment.accept(MappingResolver(), symbols)

        val validated = mappings.mapValues {
            it.value.map { mapping -> mapping to MappingValidation.of(it.key.fileEntry, mapping) }
        }

        val valids = validated.mapValues {
            it.value.filter { it.second.isValid() }
        }
        if (valids.all { it.value.isNotEmpty() }) {
            val selected = valids.mapValues {
                MappingSelector.of(it.value).select()
            }

            val generation = MappieGeneration(
                mappings = selected.mapValues { it.value!!.first }
            )
            moduleFragment.accept(MappieIrGenerator(generation), null)
        } else {
            val invalids = validated.filter { it.value.none { it.second.isValid() } }
            invalids.forEach { (function, mappings) ->
                if (mappings.isEmpty()) {
                    logError("No constructor visible to use", location(function))
                } else {
                    logAll(mappings.first().second.problems, location(function))
                }
            }
        }
    }

    companion object {
        lateinit var context: MappiePluginContext
    }
}