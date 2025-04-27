package tech.mappie.ir.reporting

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import tech.mappie.MappieContext
import java.io.File

class ReportGenerator(private val context: MappieContext) {

    private val enabled = context.configuration.reportEnabled

    private val directory by lazy { File(context.configuration.reportDir) }

    fun report(elements: List<IrElement>) {
        if (enabled) {
            runCatching { directory.mkdirs() }.getOrElse {
                context.logger.error("Mappie failed to create report output directory ${context.configuration.reportDir}.")
                throw it
            }

            elements.filterIsInstance<IrClass>().forEach { clazz ->
                val name = "${clazz.name.asString()}.kt"
                runCatching { File(directory, name).writeText(generate(clazz)) }.getOrElse {
                    context.logger.warn("Mappie failed to generate report for $name: $it")
                }
            }
        }
    }

    private fun generate(clazz: IrClass): String {
        return clazz.accept(PrettyPrinter(), KotlinStringBuilder()).print()
    }
}