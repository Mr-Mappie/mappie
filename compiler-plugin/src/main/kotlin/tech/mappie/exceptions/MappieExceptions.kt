package tech.mappie.exceptions

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.ir.IrElement
import tech.mappie.MappieContext
import tech.mappie.validation.Problem

class MappiePanicException private constructor(message: String, val origin: IrElement? = null)
    : Exception(message) {

    companion object {
        fun panic(message: String, element: IrElement? = null): Nothing {
            throw MappiePanicException(message, element)
        }
    }
}

class MappieProblemException private constructor(message: String, val origin: IrElement? = null)
    : Exception(message) {

    companion object {
        fun MappieContext.fail(message: String, element: IrElement, location: CompilerMessageLocation?): Nothing {
            logger.log(Problem.error(message, location))
            throw MappieProblemException(message, element)
        }
    }
}