package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperClassCanContainMultipleMapFunctionsTest : MappieTestCase() {

    data class Input(val text: String)
    data class Output(val text: String, val int: Int)

    @Test
    fun `mapper with multiple map functions should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassCanContainMultipleMapFunctionsTest.*

                class Mapper(private val int: Int) : ObjectMappie<Input, Output>() {
                    fun map(value: String) = value

                    override fun map(from: Input) = mapping {
                        Output::int fromValue int
                    }

                    fun map(value: Int) = value
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>("Mapper", 10).map(Input("test")))
                .isEqualTo(Output("test", 10))
        }
    }
}