package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericSourceAndTargetTest : MappieTestCase() {

    data class Input<A, B>(
        val a: A,
        val b: B,
    )

    data class Output<A, B>(
        val a: A,
        val b: B,
    )

    @Test
    fun `map generic source target properties implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceAndTargetTest.*

                class Mapper : ObjectMappie<Input<String, Int>, Output<String, Int>>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<String, Int>, Output<String, Int>>()

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
                import tech.mappie.testing.objects.generics.GenericSourceAndTargetTest.*

                class Mapper : ObjectMappie<Input<Int, String>, Output<Int, String>>() {
                    override fun map(from: Input<Int, String>) = mapping {
                        to::a fromProperty from::a
                        to::b fromValue "constant"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<Int, String>, Output<Int, String>>()

            assertThat(mapper.map(Input(1, "b")))
                .isEqualTo(Output(1, "constant"))
        }
    }

    @Test
    fun `map two generic target properties explicit with transform should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceAndTargetTest.*

                class Mapper : ObjectMappie<Input<Int, Int>, Output<String, String>>() {
                    override fun map(from: Input<Int, Int>) = mapping {
                        to::a fromProperty from::a transform { it.toString() }
                        to::b fromValue "10"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<Int, Int>, Output<String, String>>()

            assertThat(mapper.map(Input(1, 2)))
                .isEqualTo(Output("1", "10"))
        }
    }
}