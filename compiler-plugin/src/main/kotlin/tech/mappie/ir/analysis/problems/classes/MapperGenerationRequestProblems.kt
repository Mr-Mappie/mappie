package tech.mappie.ir.analysis.problems.classes

//class MapperGenerationRequestProblems(private val requests: List<GeneratedViaMapperTransformation>) {
//
//    fun all(): List<Problem> = requests
//        .filter { transformation -> isDuplicate(transformation) }
//        .flatMap { transformation ->
//            val source = transformation.source.type
//            val target = transformation.target.type
//            val requests = MappingResolver.of(source, target, ResolverContext( context.definitions, context.function))
//                .resolve(null)
//
//            if (requests.isEmpty()) {
//                listOf(
//                    Problem.error(
//                    "No implicit mapping can be generated from ${transformation.source.type.dumpKotlinLike()} to ${transformation.target.type.dumpKotlinLike()}",
//                    location(context.function),
//                    listOf("Target class has no accessible constructor"),
//                ))
//            } else {
//                val context = context.copy(generated = requests.map { it.source.type to it.target.type })
//
//                buildList {
//                    val validations = requests
//                        .associateBy { request -> MappingValidation.of(context, request) }
//
//                    if (validations.none { it.key.isValid() }) {
//                        val (validation, _) = validations.entries.first { !it.key.isValid() }
//                        val message = "No implicit mapping can be generated from ${transformation.source.type.dumpKotlinLike()} to ${transformation.target.type.dumpKotlinLike()}"
//                        add(Problem.error(message, location(context.function), validation.errors().map { it.description }))
//                    }
//                }
//            }
//        }
//
//    private fun isDuplicate(transformation: GeneratedViaMapperTransformation): Boolean =
//        context.generated.none {
//            it.first == transformation.source.type && it.second == transformation.target.type
//        }
//
//    companion object {
//        fun of(mapping: ClassMappingRequest): MapperGenerationRequestProblems {
//            val mappings = mapping.mappings.values
//                .filterSingle()
//                .filterIsInstance<TransformableClassMappingSource>()
//                .mapNotNull { it.selectGeneratedTransformationMapping() }
//
//            return MapperGenerationRequestProblems(mappings)
//        }
//    }
//}