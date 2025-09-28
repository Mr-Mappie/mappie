package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericSetMethodTest : MappieTestCase() {
    data class Input<T: Any>(val age: T)
    class Output<T : Any> {
        private lateinit var _age: T

        fun setAge(age: T) {
            this._age = age
        }
    }

    @Test
    fun `map object via generic setter explicitly should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSetMethodTest.*

                class Mapper : ObjectMappie<Input<Int>, Output<Int>>() {
                    override fun map(from: Input<Int>) = mapping {
                        to("age") fromProperty from::age
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<Int>, Output<Int>>()

            assertThat(mapper.map(Input(25)))
                .usingRecursiveComparison()
                .isEqualTo(Output<Int>().apply { setAge(25) })
        }
    }

    @Test
    fun `map object via generic setter implicitly should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSetMethodTest.*

                class Mapper : ObjectMappie<Input<Int>, Output<Int>>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<Int>, Output<Int>>()

            assertThat(mapper.map(Input(50)))
                .usingRecursiveComparison()
                .isEqualTo(Output<Int>().apply { setAge(50) })
        }
    }
}