package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ListPropertyObjectToListPropertyPrimitiveTest : MappieTestCase() {
    data class Input(val text: List<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: List<String>)

    @Test
    fun `map list explicit with implicit via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyObjectToListPropertyPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::text fromProperty from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, String>() {
                    override fun map(from: InnerInput) = from.value
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(listOf("A", "B")))
        }
    }
}