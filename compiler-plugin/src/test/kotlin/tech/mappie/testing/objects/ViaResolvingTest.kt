package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ViaResolvingTest : MappieTestCase() {
    data class Input(val text: InnerInput, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput, val int: Int)
    data class InnerOutput(val value: String)

    @Test
    fun `map without implicit without via should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ViaResolvingTest.*

                class Mapper : ObjectMappie<Input, Output>()

                class InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("inner"), 20)))
                .isEqualTo(Output(InnerOutput("inner"), 20))
        }
    }

    @Test
    fun `map without explicit without via and two inner mappers should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ViaResolvingTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::text fromProperty from::text
                    }
                }

                class InnerMapperA : ObjectMappie<InnerInput, InnerOutput>()
                class InnerMapperB : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessages(
                6 to "Multiple mappers resolved to be used in an implicit via",
                6 to "Target Output::text of type InnerOutput cannot be assigned from from::text of type InnerInput",
            )
        }
    }

    @Test
    fun `map without implicit without via and two inner mappers should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ViaResolvingTest.*

                class Mapper : ObjectMappie<Input, Output>()

                class InnerMapperA : ObjectMappie<InnerInput, InnerOutput>()
                class InnerMapperB : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessages(
                4 to "Multiple mappers resolved to be used in an implicit via",
                4 to "Target Output::text automatically resolved from Input::text but cannot assign source type InnerInput to target type InnerOutput",
            )
        }
    }

    @Test
    fun `mapper prioritizes exact inner mapper over nullable alternative`() {
        compile {
            file(
                "Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ViaResolvingTest.*

                class Mapper : ObjectMappie<Input, Output>()

                class ExactInnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                class NullableInnerMapper : ObjectMappie<InnerInput?, InnerOutput?>()
                """.trimIndent(),
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(InnerInput("inner"), 10)))
                .isEqualTo(Output(InnerOutput("inner"), 10))
        }
    }
}