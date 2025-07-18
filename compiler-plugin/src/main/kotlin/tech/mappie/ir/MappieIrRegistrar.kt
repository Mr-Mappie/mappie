package tech.mappie.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.name.ClassId
import tech.mappie.MappieState
import tech.mappie.fir.resolving.ClassMapping
import tech.mappie.fir.resolving.EnumMapping
import tech.mappie.fir.resolving.Mapping
import tech.mappie.fir.resolving.classes.ExplicitPropertySource
import tech.mappie.fir.resolving.classes.NamedValueParameterTarget
import tech.mappie.fir.resolving.classes.ImplicitPropertySource
import tech.mappie.fir.resolving.classes.ValueParameterTarget
import tech.mappie.fir.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.fir.resolving.enums.ThrownByEnumMappingTarget
import tech.mappie.ir.generation.ClassTransformer
import tech.mappie.ir_old.util.isMappieMapFunction

data class CodeGenerationContext(val pluginContext: IrPluginContext)

class MappieIrRegistrar(private val state: MappieState) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val context = CodeGenerationContext(pluginContext)
        val factory = MappieCodeGenerationModelFactory(context)

        state.models.forEach { (classId, mapping) ->
            val model = factory.construct(classId, mapping)

            val clazz = pluginContext.referenceClass(classId)
            clazz?.owner?.apply { transform(ClassTransformer(context, model), null) }
        }
    }
}

class MappieCodeGenerationModelFactory(val context: CodeGenerationContext) {

    fun construct(classId: ClassId, mapping: Mapping) = when (mapping) {
        is ClassMapping -> construct(classId, mapping)
        is EnumMapping -> construct(classId, mapping)
    }

    fun construct(classId: ClassId, mapping: EnumMapping): CodeGenerationModel {
        val clazz = context.pluginContext.referenceClass(classId)!!
        val (sourceClass, targetClass) = ((clazz.superTypes().single().type as IrSimpleType).arguments.map { it.typeOrFail.classOrFail })

        return UserDefinedEnumCodeGenerationModel(
            clazz.functions.single { it.owner.isMappieMapFunction() }.owner.parameters.single { it.kind == IrParameterKind.Regular },
            targetClass.typeWith(emptyList()),
            mapping.mappings.map { (target, source) ->
                when (target) {
                    is ResolvedEnumMappingTarget -> {
                        val target = targetClass.owner.declarations.filterIsInstance<IrEnumEntry>().first { it.name == target.entry.name }
                        val source = sourceClass.owner.declarations.filterIsInstance<IrEnumEntry>().first { it.name == source.name }
                        IrEnumEntryTarget(target) to source
                    }
                    is ThrownByEnumMappingTarget -> TODO()
                    null -> TODO()
                }
            }.toMap()
        )
    }

    fun construct(classId: ClassId, mapping: ClassMapping): CodeGenerationModel {
        val clazz = context.pluginContext.referenceClass(classId)!!
        val (sourceClass, targetClass) = ((clazz.superTypes().single().type as IrSimpleType).arguments.map { it.typeOrFail.classOrFail })
        val constructor = targetClass.constructors.single()

        val mappings = mapping.mappings.map { (target, source) ->
            val target = when (target) {
                is ValueParameterTarget -> {
                    IrValueParameterClassTarget(
                        parameter = constructor.owner.parameters.find { it.name == target.parameter.name }!!,
                    )
                }
                is NamedValueParameterTarget -> {
                    TODO()
                }
            }
            val source = when (source!!) {
                is ImplicitPropertySource -> {
                    IrImplicitPropertyClassSource(
                        receiver = clazz.functionByName("map").owner.parameters.single { it.kind == IrParameterKind.Regular },
                        property = sourceClass.owner.properties.first { it.name == source.property.name },
                    )
                }
                is ExplicitPropertySource -> {
                    val receiver = source.reference.explicitReceiver
                    if (receiver != null) {
                        IrExplicitPropertyClassSource()
                    } else {
                        IrImplicitPropertyClassSource(
                            receiver = clazz.functionByName("map").owner.parameters.single { it.kind == IrParameterKind.Regular },
                            property = sourceClass.owner.properties.first { it.name == source.reference.calleeReference.name },
                        )
                    }
                }
            }
            target to source
        }

        return UserDefinedClassCodeGenerationModel(
            constructor.owner,
            mappings.toMap()
        )
    }
}
