package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GeneratedClassNonNullToNullTest : MappieTestCase() {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: String)
    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: String?)

    @Test
    fun `map object with nested non-nullable to nullable without declaring mapping should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassNonNullToNullTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("value"))))
                .isEqualTo(Output(InnerOutput("value")))
        }
    }
}