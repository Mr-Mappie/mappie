package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class NestedNonNullToNullPropertyTest : MappieTestCase() {

    data class Input(val text: InnerInput, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput?, val int: Int)
    data class InnerOutput(val value: String)

    @Test
    fun `map object with nested non-null to null implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNonNullToNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("value"), 30)))
                .isEqualTo(Output(InnerOutput("value"), 30))
        }
    }

    @Test
    fun `map object with nested non-null to null explicit without via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNonNullToNullPropertyTest.*

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

            assertThat(mapper.map(Input(InnerInput("value"), 30)))
                .isEqualTo(Output(InnerOutput("value"), 30))
        }
    }

    @Test
    fun `map object with nested non-null to null explicit fromPropertyNotNull without via should warn`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNonNullToNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromPropertyNotNull from::text
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(6, "Unnecessary fromPropertyNotNull for non-nullable type InnerInput",
                listOf("Use fromProperty instead of fromPropertyNotNull")
            )

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("value"), 30)))
                .isEqualTo(Output(InnerOutput("value"), 30))
        }
    }

    @Test
    fun `map object with nested non-null to null explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNonNullToNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text via InnerMapper
                    }
                }

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("value"), 30)))
                .isEqualTo(Output(InnerOutput("value"), 30))
        }
    }
}