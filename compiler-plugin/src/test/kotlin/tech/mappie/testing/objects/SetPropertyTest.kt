package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class SetPropertyTest : MappieTestCase() {
    data class InputWithoutValue(val age: Int)
    data class Input(val value: String)
    class Output {
        var value: String? = null
    }

    @Test
    fun `set property without value should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetPropertyTest.*

                class Mapper : ObjectMappie<InputWithoutValue, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<InputWithoutValue, Output>()

            assertThat(mapper.map(InputWithoutValue(10)))
                .usingRecursiveComparison()
                .isEqualTo(Output())
        }
    }

    @Test
    fun `set property with value explicitly automatically should succeed`() {
        compile {
            file("Mapper.kt",
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
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("value")))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { value = "value" })
        }
    }

    @Test
    fun `set property with value resolved automatically should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("value")))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { value = "value" })
        }
    }
}