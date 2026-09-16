package tech.mappie.ir.reporting

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.MappieIrGenerationProblems.MAPPIE_FAILED_TO_CREATE_REPORT_FILE
import tech.mappie.ir.generation.MappieIrGenerationProblems.MAPPIE_INCOMPREHENSIBLE_REPORT_FILE
import java.io.File
import java.io.IOException

class ReportGenerator {

    context (context: MappieContext)
    fun report(elements: List<IrClass>) {
        if (context.configuration.reportEnabled) {
            val directory = File(context.configuration.reportDir)

            runCatching { directory.mkdirs() }.getOrElse {
                throw IOException("Mappie failed to create report output directory ${context.configuration.reportDir}.", it)
            }

            elements.forEach { clazz ->
                val file = File(directory, "${clazz.name.asString()}.kt")

                try {
                    file.writeText(generate(clazz))
                } catch (_: IOException) {
                    context.at(clazz).report(MAPPIE_FAILED_TO_CREATE_REPORT_FILE)
                } catch (_: Exception) {
                    context.at(clazz).report(MAPPIE_INCOMPREHENSIBLE_REPORT_FILE)
                    runCatching { file.writeText(clazz.dumpKotlinLike()) }
                }
            }
        } else if (context.configuration.isMappieDebugMode) {
            elements.forEach { clazz ->
                context.messageCollector.report(CompilerMessageSeverity.INFO, "${clazz.name.asString()}.kt ${System.lineSeparator()}${generate(clazz)}")
            }
        }
    }

    private fun generate(clazz: IrClass): String =
        clazz.pretty()
}