package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ListPropertyToNullableListPropertyTest : MappieTestCase() {
    data class Input(val text: List<InnerInput>, val int: Int)
    data class InnerInput(val value: String)

    data class Output(val text: List<InnerOutput>?, val int: Int)
    data class InnerOutput(val value: String)

    @Test
    fun `map nested non-nullable list to nullable list explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyToNullableListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text via InnerMapper.forList
                    }
                }

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
    fun `map nested non-nullable list to nullable list explicit without via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyToNullableListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text
                    }
                }

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
    fun `map nested non-nullable list to nullable list implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyToNullableListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()

                object SecondInnerMapper : ObjectMappie<InnerOutput, InnerInput>()
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