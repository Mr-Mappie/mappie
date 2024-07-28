package tech.mappie.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.*
import tech.mappie.BaseVisitor
import tech.mappie.api.Mappie
import tech.mappie.util.*

class MappieDefinitionsCollector : BaseVisitor<MappieDefinitions, Unit>(null) {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit) =
        MappieDefinitions(declaration.files.flatMap { it.accept(data).toList() })

    override fun visitFile(declaration: IrFile, data: Unit): MappieDefinitions {
        file = declaration.fileEntry
        return MappieDefinitions(declaration.declarations.flatMap { it.accept(data).toList() })
    }

    override fun visitClass(declaration: IrClass, data: Unit) =
        MappieDefinitions(buildList {
            if (declaration.isStrictSubclassOf(Mappie::class)) {
                (declaration.superTypes.single() as? IrSimpleType)?.let {
                    add(MappieDefinition(declaration))
                }
            }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        })

    override fun visitElement(element: IrElement, data: Unit) =
        MappieDefinitions(emptyList())
}