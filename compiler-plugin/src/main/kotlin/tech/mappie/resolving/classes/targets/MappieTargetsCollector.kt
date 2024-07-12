package tech.mappie.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.properties

//class MappieTargetsCollector(val constructor: IrConstructor) {
//
//    val constructParameters: List<MappieTarget>
//        get() = constructor?.valueParameters?.map { MappieValueParameterTarget(it) } ?: emptyList()
//
//    val setters: Sequence<MappieTarget>
//        get() = targetType.classOrFail.owner.properties
//            .filter { it.setter != null && it.name.toString() !in constructParameters.map { it.name.toString() } }
//            .map { MappieSetterTarget(it) }
//}

//class MappieTargetsCollector(file: IrFileEntry) : BaseVisitor<Map<IrConstructor, List<MappieTarget>>, Unit>(file) {
//
//    override fun visitConstructor(declaration: IrConstructor, data: Unit): Map<IrConstructor, List<MappieTarget>> {
//        return mapOf(declaration to declaration.valueParameters.map { MappieValueParameterTarget(it) })
//    }
//}
//
//class MappieSettersCollector(file: IrFileEntry) : BaseVisitor<List<MappieTarget>, Unit>(file) {
//
//}