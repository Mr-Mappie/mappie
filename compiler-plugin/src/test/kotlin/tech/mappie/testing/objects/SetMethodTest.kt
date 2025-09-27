package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class SetMethodTest : MappieTestCase() {

    data class Input(val age: Int)
    class Output {
        private var _age: Int = 0

        fun setAge(age: Int) {
            this._age = age
        }
    }

    @Test
    fun `map object via setter explicitly should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetMethodTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to("age") fromProperty from::age
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input(25)))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { setAge(25) })
        }
    }

    @Test
    fun `map object via setter implicitly should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.SetMethodTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input(50)))
                .usingRecursiveComparison()
                .isEqualTo(Output().apply { setAge(50) })
        }
    }
}