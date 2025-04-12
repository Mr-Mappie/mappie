package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GenericGetMethodTest {
    class Input<T : Any> {
        private lateinit var _value: T

        fun setValue(age: T) {
            this._value = age
        }
        fun getValue() = _value
    }

    data class Output<T>(val value: T)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object via generic getter explicitly should succeed`() {
        compile(directory) {
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
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input<String>, Output<String>>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input<String>().apply { setValue("string") }))
                .usingRecursiveComparison()
                .isEqualTo(Output("string"))
        }
    }

    @Test
    fun `map object via generic getter implicitly should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericGetMethodTest.*

                class Mapper : ObjectMappie<Input<String>, Output<String>>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input<String>, Output<String>>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input<String>().apply { setValue("value") }))
                .usingRecursiveComparison()
                .isEqualTo(Output("value"))
        }
    }
}