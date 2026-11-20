package tech.mappie.ir.reporting

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.IrMappieGeneratedClass
import tech.mappie.ir.generation.MappieIrGenerationProblems.MAPPIE_FAILED_TO_CREATE_REPORT_FILE
import tech.mappie.ir.generation.MappieIrGenerationProblems.MAPPIE_INCOMPREHENSIBLE_REPORT_FILE
import java.io.File
import java.io.IOException

class ReportGenerator {

    context (context: MappieContext)
    fun report(elements: List<IrClass>) {
        if (!context.configuration.reportEnabled && !context.configuration.isMappieDebugMode) return

        val helpers = context.definitions.generated.map { it.clazz }.filterNot { it is IrMappieGeneratedClass }
        val reported = elements.toSet() + helpers
        val detached = helpers.filter { helper ->
            // Nested helpers already appear in their enclosing class's report.
            generateSequence(helper.parentClassOrNull) { it.parentClassOrNull }.none { it in reported }
        }
        val reportRoots = elements + detached

        if (context.configuration.reportEnabled) {
            val directory = File(context.configuration.reportDir)

            runCatching { directory.mkdirs() }.getOrElse {
                throw IOException("Mappie failed to create report output directory ${context.configuration.reportDir}.", it)
            }

            reportRoots.forEach { clazz ->
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
        } else {
            reportRoots.forEach { clazz ->
                context.messageCollector.report(CompilerMessageSeverity.INFO, "${clazz.name.asString()}.kt ${System.lineSeparator()}${generate(clazz)}")
            }
        }
    }

    private fun generate(clazz: IrClass): String =
        clazz.pretty()
}
