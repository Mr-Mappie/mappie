package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import tech.mappie.testing.compilation.compile

class NullableListPropertyToListPropertyTest : MappieTestCase() {
    data class Input(val text: List<InnerInput>?)
    data class InnerInput(val value: String)

    data class Output(val text: List<InnerOutput>)
    data class InnerOutput(val value: String)

    @Test
    fun `map nested nullable list to non-nullable list explicit with transform should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.NullableListPropertyToListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromPropertyNotNull from::text transform { InnerMapper.mapList(it) }
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(InnerInput("first"), InnerInput("second")))))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }

    @Test
    fun `map nested nullable list to non-nullable list explicit with via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.collections.*
                import tech.mappie.testing.lists.NullableListPropertyToListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromPropertyNotNull from::text via IterableToListMapper(InnerMapper)
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(InnerInput("first"), InnerInput("second")))))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }

    @Test
    fun `map nested nullable list to non-nullable list explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.NullableListPropertyToListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromPropertyNotNull from::text
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(InnerInput("first"), InnerInput("second")))))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }

    @Test
    fun `map nested nullable list to non-nullable list explicit without via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.NullableListPropertyToListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromPropertyNotNull from::text
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(InnerInput("first"), InnerInput("second")))))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }

    @Test
    fun `map nested nullable list to non-nullable list implicit should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.NullableListPropertyToListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "Target Output::text automatically resolved from Input::text but cannot assign source type List<InnerOutput>? to target type List<InnerOutput>")
        }
    }
}