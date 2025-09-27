package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class NestedNullToNullPropertyTest : MappieTestCase() {

    data class Input(val text: InnerInput?, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput?, val int: Int)
    data class InnerOutput(val value: String)

    @Test
    fun `map nested nullable to nullable implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNullPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("value"), 20)))
                .isEqualTo(Output(InnerOutput("value"), 20))

            assertThat(mapper.map(Input(null, 20)))
                .isEqualTo(Output(null, 20))
        }
    }

    @Test
    fun `map nested nullable to nullable explicit without via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNullPropertyTest.*

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

            assertThat(mapper.map(Input(InnerInput("value"), 20)))
                .isEqualTo(Output(InnerOutput("value"), 20))

            assertThat(mapper.map(Input(null, 20)))
                .isEqualTo(Output(null, 20))
        }
    }

    @Test
    fun `map nested nullable to nullable explicit fromPropertyNotNull without via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNullPropertyTest.*

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
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("value"), 20)))
                .isEqualTo(Output(InnerOutput("value"), 20))
        }
    }

    @Test
    fun `map nested nullable to nullable null explicit fromPropertyNotNull without via should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNullPropertyTest.*

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
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThatThrownBy { mapper.map(Input(null, 20)) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Reference from::text must be non-null.")

        }
    }

    @Test
    fun `map nested nullable to nullable explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedNullToNullPropertyTest.*

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

            assertThat(mapper.map(Input(InnerInput("value"), 20)))
                .isEqualTo(Output(InnerOutput("value"), 20))

            assertThat(mapper.map(Input(null, 20)))
                .isEqualTo(Output(null, 20))
        }
    }
}