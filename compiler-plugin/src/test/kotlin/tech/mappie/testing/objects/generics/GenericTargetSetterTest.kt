package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericTargetSetterTest : MappieTestCase() {

    data class Input(
        val a: String,
        val b: Int,
    )

    data class Output<A, B>(val b: B) {
        var a: A? = null
    }

    @Test
    fun `map to generic target setter should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericTargetSetterTest.*

                class Mapper : ObjectMappie<Input, Output<String, Int>>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output<String, Int>>()

            assertThat(mapper.map(Input("a", 1)))
                .isEqualTo(Output<String, Int>(1).apply { a = "a" })
        }
    }
}