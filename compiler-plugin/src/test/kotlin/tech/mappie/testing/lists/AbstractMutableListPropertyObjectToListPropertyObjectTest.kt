package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class AbstractMutableListPropertyObjectToListPropertyObjectTest : MappieTestCase() {

    data class Input(val text: AbstractMutableList<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: List<InnerOutput>)
    data class InnerOutput(val value: String)

    @Test
    fun `map abstract mutable list implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.AbstractMutableListPropertyObjectToListPropertyObjectTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            val list = object : AbstractMutableList<InnerInput>() {
                override val size = 2

                override fun get(index: Int) = when(index) {
                    0 -> InnerInput("first")
                    1 -> InnerInput("second")
                    else -> throw IndexOutOfBoundsException()
                }

                override fun add(index: Int, element: InnerInput) = error("")
                override fun removeAt(index: Int) = error("")
                override fun set(index: Int, element: InnerInput) = error("")
            }

            assertThat(mapper.map(Input(list)))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }
}