package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GenericGetMethodTest : MappieTestCase() {
    class Input<T : Any> {
        private lateinit var _value: T

        fun setValue(age: T) {
            this._value = age
        }
        fun getValue() = _value
    }

    data class Output<T>(val value: T)

    @Test
    fun `map object via generic getter explicitly should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericGetMethodTest.*

                class Mapper : ObjectMappie<Input<String>, Output<String>>() {
                    override fun map(from: Input<String>) = mapping {
                        to::value fromValue from.getValue()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<String>, Output<String>>()

            assertThat(mapper.map(Input<String>().apply { setValue("string") }))
                .usingRecursiveComparison()
                .isEqualTo(Output("string"))
        }
    }

    @Test
    fun `map object via generic getter implicitly should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericGetMethodTest.*

                class Mapper : ObjectMappie<Input<String>, Output<String>>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input<String>, Output<String>>()

            assertThat(mapper.map(Input<String>().apply { setValue("value") }))
                .usingRecursiveComparison()
                .isEqualTo(Output("value"))
        }
    }
}