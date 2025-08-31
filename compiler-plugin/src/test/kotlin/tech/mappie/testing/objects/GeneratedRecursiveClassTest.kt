package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GeneratedRecursiveClassTest : MappieTestCase() {
    data class Input(val value: LinkedListInput)
    data class LinkedListInput(val value: String, val next: LinkedListInput?)
    data class Output(val value: LinkedListOutput)
    data class LinkedListOutput(val value: String, val next: LinkedListOutput?)

    @Test
    fun `map object with nested recursive class without declaring mapping should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedRecursiveClassTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(LinkedListInput("a", LinkedListInput("b", null)))))
                .isEqualTo(Output(LinkedListOutput("a", LinkedListOutput("b", null))))
        }
    }
}