package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GeneratedDoubleNestedClassTest : MappieTestCase() {
    data class Input(val a: InnerInput, val b: InnerInput?)
    data class InnerInput(val value: InnerInput?)
    data class Output(val a: InnerOutput, val b: InnerOutput?)
    data class InnerOutput(val value: InnerOutput?)

    @Test
    fun `map object with nested class without declaring mapping should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedDoubleNestedClassTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput(InnerInput(null)), null)))
                .isEqualTo(Output(InnerOutput(InnerOutput(null)), null))
        }
    }
}