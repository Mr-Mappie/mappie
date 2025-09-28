package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class TwoGenericTargetPropertiesTest : MappieTestCase() {

    data class Input(
        val a: String,
        val b: Int,
    )

    data class Output<A, B>(
        val a: A,
        val b: B,
    )

    @Test
    fun `map two generic target properties implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.TwoGenericTargetPropertiesTest.*

                class Mapper : ObjectMappie<Input, Output<String, Int>>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output<String, Int>>()

            assertThat(mapper.map(Input("a", 1)))
                .isEqualTo(Output("a", 1))
        }
    }

    @Test
    fun `map two generic target properties explicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.TwoGenericTargetPropertiesTest.*

                class Mapper : ObjectMappie<Input, Output<String, Int>>() {
                    override fun map(from: Input) = mapping {
                        to::a fromValue "constant"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output<String, Int>>()

            assertThat(mapper.map(Input("a", 1)))
                .isEqualTo(Output("constant", 1))
        }
    }
}