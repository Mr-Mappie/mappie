package tech.mappie.ir.postprocessing

import org.jetbrains.kotlin.ir.util.packageFqName
import tech.mappie.MappieContextFileManager
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.CodeGenerationResult
import tech.mappie.ir.reporting.ReportGenerator

object PostProcessingStage {

    context(context: MappieContext)
    fun execute(generated: CodeGenerationResult) {
        val incremental = context.definitions.run {
            val definitions = internal + internalNonGenerated + internalIncremental

            definitions.map { it.origin.clazz }.toSet().map {
                (it.packageFqName?.asString() ?: "") + it.name.asString()
            }
        }

        MappieContextFileManager.write(
            context.persistent.copy(
                incremental = incremental
            )
        )

        ReportGenerator().report(generated.classes)
    }
}