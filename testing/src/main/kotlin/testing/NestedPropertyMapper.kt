package testing

import tech.mappie.api.ObjectMappie

data class NestedInput(val input: NestedInputValue)

data class NestedInputValue(val value: String)

data class NestedOutput(val value: String)

object NestedPropertyMapper : ObjectMappie<NestedInput, NestedOutput>() {
    override fun map(from: NestedInput): NestedOutput = mapping {
        NestedOutput::value fromProperty from.input::value
    }
}