package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class LinkedHashSetTest : MappieTestCase() {

    data class Input(val text: LinkedHashSet<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: LinkedHashSet<InnerOutput>)
    data class InnerOutput(val value: String)

    @Test
    fun `map object with LinkedHashSet should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.LinkedHashSetTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            val content = setOf("a", "b")

            assertThat(mapper.map(Input(LinkedHashSet(content.map { InnerInput(it) }))))
                .isEqualTo(Output(LinkedHashSet(content.map { InnerOutput(it) })))
        }
    }
}