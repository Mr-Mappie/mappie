package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GeneratedClassNullToNullTest : MappieTestCase() {
    data class Input(val a: InnerInput?)
    @Suppress("unused") enum class InnerInput { FIRST, SECOND }
    data class Output(val a: InnerOutput?)
    @Suppress("unused") enum class InnerOutput { FIRST, SECOND }

    @Test
    fun `map object with nested nullable to nullable without declaring mapping should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassNullToNullTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput.FIRST)))
                .isEqualTo(Output(InnerOutput.FIRST))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }
}