package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericWithUpperBoundTest : MappieTestCase() {
    interface InputInterface { val id: String }
    data class Input(override val id: String) : InputInterface
    data class Output(val id: String)

    @Test
    fun `map from generic interface upper bound implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GenericWithUpperBoundTest.*

                class Mapper<FROM : InputInterface> : ObjectMappie<FROM, Output>()
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
    fun `map from generic interface upper bound explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GenericWithUpperBoundTest.*

                class Mapper<FROM : InputInterface> : ObjectMappie<FROM, Output>() {
                    override fun map(from: FROM) = mapping {
                        to::id fromProperty from::id
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input("value")))
                .isEqualTo(Output("value"))
        }
    }
}