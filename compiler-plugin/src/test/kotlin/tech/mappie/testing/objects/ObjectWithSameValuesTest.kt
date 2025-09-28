package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ObjectWithSameValuesTest : MappieTestCase() {

    data class Input(val value: String)
    data class Output(val value: String)

    @Test
    fun `map identical objects should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithSameValuesTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input("value")))
                .isEqualTo(Output("value"))
        }
    }

    @Test
    @Disabled("Not implemented yet")
    fun `map identical objects with an explicit mapping should warn`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithSameValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::value fromProperty Input::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(6, "Unnecessary explicit mapping of target Output::value")

            assertThat(objectMappie<Input, Output>().map(Input("value")))
                .isEqualTo(Output("value"))
        }
    }
}