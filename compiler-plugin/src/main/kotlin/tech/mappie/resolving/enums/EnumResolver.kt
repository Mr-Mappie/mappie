package tech.mappie.resolving.enums

import tech.mappie.resolving.EnumMapping
import tech.mappie.util.location
import tech.mappie.util.logWarn
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*

class EnumResolver(private val declaration: IrFunction) {

    private val targetType = declaration.returnType

    private val sourceType = declaration.valueParameters.first().type

    init {
        check(targetType.getClass()!!.isEnumClass)
    }

    fun resolve(): EnumMapping {
        val constructor = EnumMappingsConstructor.of(targetType, sourceType).apply {
            targets.addAll(targetType.getClass()!!.accept(EnumEntriesCollector(declaration.fileEntry), Unit))
            sources.addAll(sourceType.getClass()!!.accept(EnumEntriesCollector(declaration.fileEntry), Unit))
        }

        declaration.body?.accept(EnumMappingBodyCollector(declaration.fileEntry), constructor)

        validate(constructor)

        return constructor.construct()
    }

    private fun validate(constructor: EnumMappingsConstructor) {
        constructor.sources.forEach { source ->
            val resolved = constructor.targets.filter { target -> target.name == source.name }
            val explicit = constructor.explicit[source]

            if (resolved.isNotEmpty() && explicit != null) {
                when (val mapping = explicit.first()) {
                    is ExplicitEnumMappingTarget -> {
                        val target = "${mapping.target.symbol.owner.callableId.className}.${mapping.target.name.asString()}"
                        logWarn("Unnecessary explicit mapping of target $target", location(declaration.fileEntry, mapping.origin))
                    }
                    else -> Unit
                }
            }
        }
    }
}

