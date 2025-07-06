package tech.mappie.ir_old.reporting

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.kotlinFqName
import tech.mappie.MappieContext
import tech.mappie.ir_old.util.location
import java.io.File
import java.io.IOException

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
                val file = File(directory, "${clazz.name.asString()}.kt")

                try {
                    file.writeText(generate(clazz))
                } catch (_: IOException) {
                    context.logger.error("Mappie failed to create report file ${file.path}.", location(clazz))
                } catch (_: Exception) {
                    context.logger.onlyWarn(
                        "Mappie failed to generate comprehensible report for ${clazz.kotlinFqName.asString()}.",
                        location(clazz)
                    )
                    runCatching { file.writeText(clazz.dumpKotlinLike()) }
                }
            }
        } else if (context.configuration.isMappieDebugMode) {
            elements.filterIsInstance<IrClass>().forEach { clazz ->
                val name = "${clazz.name.asString()}.kt"
                context.logger.logging(name + System.lineSeparator() + generate(clazz))
            }
        }
    }

    private fun generate(clazz: IrClass): String =
        clazz.accept(PrettyPrinter(), KotlinStringBuilder()).print()
}