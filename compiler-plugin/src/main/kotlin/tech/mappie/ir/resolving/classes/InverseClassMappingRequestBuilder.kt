package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.MappieContext
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.resolving.*
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.sources.ImplicitClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget

class InverseClassMappingRequestBuilder(constructor: IrConstructor) : ClassMappingRequestBuilder(constructor) {

    private val inverseTargets = mutableListOf<ClassMappingTarget>()

    private val inverseSources = mutableMapOf<Name, IrType>()

    private val inverseImplicit = mutableMapOf<Name, List<ImplicitClassMappingSource>>()

    private val inverseExplicit = mutableMapOf<Name, List<ExplicitClassMappingSource>>()

    context(context: MappieContext)
    fun construct(origin: InternalMappieDefinition): ClassMappingRequest {

    }
}
