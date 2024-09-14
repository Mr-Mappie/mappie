package tech.mappie.generation

import tech.mappie.MappieContext

class CodeGenerationContext(
    context: MappieContext,
    val model: CodeGenerationModel,
) : MappieContext by context