package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ObjectWithSetPrimitiveToObjectSetPrimitiveTest : MappieTestCase() {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: Set<String>)

    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: Set<String>)

    @Test
    fun `map object with nested set with generated mapper should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.ObjectWithSetPrimitiveToObjectSetPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>("Mapper")

            assertThat(mapper.map(Input(InnerInput(setOf("first", "second")))))
                .isEqualTo(Output(InnerOutput(setOf("first", "second"))))
        }
    }
}