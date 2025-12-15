package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.OffsetDateTime

class GeneratedClassWithoutVisibleConstructorTest : MappieTestCase() {

    data class Input(val value: LocalDate?)
    data class Output(val value: OffsetDateTime?)

    @Test
    fun `map nullable implicit should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassWithoutVisibleConstructorTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "No implicit mapping can be generated from LocalDate? to OffsetDateTime?",
                listOf("Target class Output has no visible constructor")
            )
        }
    }

    @Test
    fun `map nullable explicit from null should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassWithoutVisibleConstructorTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromValue null
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(LocalDate.now())))
                .isEqualTo(Output(null))

        }
    }
}