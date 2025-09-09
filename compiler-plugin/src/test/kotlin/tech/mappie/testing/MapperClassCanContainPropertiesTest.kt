package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperClassCanContainPropertiesTest : MappieTestCase() {

    data class Input(val text: String)
    data class Output(val text: String, val int: Int)

    @Test
    fun `map with a property in constructor of mapper should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassCanContainPropertiesTest.*

                class Mapper(private val int: Int) : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::int fromValue int
                    }
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