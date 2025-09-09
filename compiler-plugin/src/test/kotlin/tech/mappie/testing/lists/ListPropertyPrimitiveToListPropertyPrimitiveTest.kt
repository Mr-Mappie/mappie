package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ListPropertyPrimitiveToListPropertyPrimitiveTest : MappieTestCase() {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: List<String>)

    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: List<String>)

    @Test
    fun `map object with nested list implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyPrimitiveToListPropertyPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput(listOf("first", "second")))))
                .isEqualTo(Output(InnerOutput(listOf("first", "second"))))
        }
    }
}