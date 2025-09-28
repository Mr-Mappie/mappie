package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GeneratedDoubleClassTest : MappieTestCase() {
    data class Input(val a: InnerInput, val b: InnerInput)
    data class InnerInput(val value: String)
    data class Output(val a: InnerOutput, val b: InnerOutput)
    data class InnerOutput(val value: String)

    @Test
    fun `map object with nested class without declaring mapping should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedDoubleClassTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("a"), InnerInput("b"))))
                .isEqualTo(Output(InnerOutput("a"), InnerOutput("b")))
        }
    }
}