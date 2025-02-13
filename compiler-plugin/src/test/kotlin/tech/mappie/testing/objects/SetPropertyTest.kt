package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class SetPropertyTest {
    data class InputWithoutValue(val age: Int)
    data class Input(val value: String)
    class Output {
        var value: String? = null
    }

    @TempDir
    lateinit var directory: File

    @Test
    fun `set property without value should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetPropertyTest.*

                class Mapper : ObjectMappie<InputWithoutValue, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<InputWithoutValue, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(InputWithoutValue(10)))
                .usingRecursiveComparison()
                .isEqualTo(Output())
        }
    }

    @Test
    fun `set property with value explicitly automatically should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("value")))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { value = "value" })
        }
    }

    @Test
    fun `set property with value resolved automatically should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("value")))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { value = "value" })
        }
    }
}