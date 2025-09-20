package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericSourceTest : MappieTestCase() {

    data class Input<A, B>(
        val a: A,
        val b: B,
    )

    data class Output(
        val a: Int,
        val b: String,
    )

    @Test
    fun `map from generic source property implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceTest.*

                class Mapper : ObjectMappie<Input<Int, String>, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<Int, String>, Output>()

            assertThat(mapper.map(Input(1, "b")))
                .isEqualTo(Output(1, "b"))
        }
    }

    @Test
    fun `map from generic source property explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceTest.*

                class Mapper : ObjectMappie<Input<Int, String>, Output>() {
                    override fun map(from: Input<Int, String>) = mapping {
                        to::b fromValue "constant"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<Int, String>, Output>()

            assertThat(mapper.map(Input(1, "b")))
                .isEqualTo(Output(1, "constant"))
        }
    }
}