package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MutableListPropertyObjectToListPropertyObjectTest : MappieTestCase() {
    data class Input(val text: MutableList<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: List<InnerOutput>)
    data class InnerOutput(val value: String)

    @Test
    fun `map mutable list implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MutableListPropertyObjectToListPropertyObjectTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(mutableListOf(InnerInput("first"), InnerInput("second")))))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }
}