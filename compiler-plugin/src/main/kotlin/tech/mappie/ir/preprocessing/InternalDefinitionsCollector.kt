package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.InternalMappieDefinition
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.ir.util.isSubclassOf
import tech.mappie.ir.referenceMappieClass

class InternalDefinitionsCollector(val context: MappieContext) : BaseVisitor<List<InternalMappieDefinition>, Unit>() {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit) =
        declaration.files.flatMap { it.accept(data) }

    override fun visitFile(declaration: IrFile, data: Unit): List<InternalMappieDefinition> {
        return declaration.declarations.flatMap { it.accept(data) }
    }

    override fun visitClass(declaration: IrClass, data: Unit) =
        buildList {
                context(context) {
                    if (declaration.isSubclassOf(context.referenceMappieClass())) {
                        add(InternalMappieDefinition(declaration))
                    }
                }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        }

    override fun visitElement(element: IrElement, data: Unit) =
        emptyList<InternalMappieDefinition>()
}