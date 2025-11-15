package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ListPropertyObjectToListPropertyObjectTest : MappieTestCase() {
    data class Input(val text: List<InnerInput>, val int: Int)
    data class InnerInput(val value: String)

    data class Output(val text: List<InnerOutput>, val int: Int)
    data class InnerOutput(val value: String)

    @Test
    fun `map nested list implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyObjectToListPropertyObjectTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(InnerInput("first"), InnerInput("second")), 20)))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second")), 20))
        }
    }

    @Test
    fun `map nested list with extra mapper defined should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyObjectToListPropertyObjectTest.*

                class IrrelevantMapper : ObjectMappie<List<String>, List<Int>>() {
                    override fun map(from: List<String>) = kotlin.error("wrong mapper called")
                }

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(InnerInput("first"), InnerInput("second")), 20)))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second")), 20))
        }
    }
}