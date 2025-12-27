package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MultipleMappersSameGeneratedOutputTest : MappieTestCase() {

    data class Input1(val nested: NestedInput)
    data class Input2(val nested: NestedInput)

    data class NestedInput(val value: NestedNestedInput)
    data class NestedNestedInput(val value: String)

    data class Output(val nested: NestedOutput)
    data class NestedOutput(val value: NestedNestedOutput)
    data class NestedNestedOutput(val value: String)

    @Test
    fun `multiple mappers same generated output should succeed`() {
        compile(verbose = true) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.MultipleMappersSameGeneratedOutputTest.*

                class Mapper1: ObjectMappie<Input1, Output>()
                class Mapper2: ObjectMappie<Input2, Output>()
            """)
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }
}