package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class NonNullablePropertyToNullablePropertyTest : MappieTestCase() {

    data class Input(val value: String)
    data class Output(val value: String?)

    @Test
    fun `map object with non-nullable property to nullable property should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NonNullablePropertyToNullablePropertyTest.*

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
}