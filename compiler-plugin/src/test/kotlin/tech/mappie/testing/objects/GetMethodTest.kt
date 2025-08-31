package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GetMethodTest : MappieTestCase() {
    class Input {
        private var _age: Int = 0

        fun setAge(age: Int) {
            this._age = age
        }
        fun getAge() = _age
    }

    data class Output(val age: Int)

    @Test
    fun `map object via getter explicitly should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GetMethodTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::age fromValue from.getAge()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input().apply { setAge(20) }))
                .usingRecursiveComparison()
                .isEqualTo(Output(20))
        }
    }

    @Test
    fun `map object via getter implicitly should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GetMethodTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input().apply { setAge(60) }))
                .usingRecursiveComparison()
                .isEqualTo(Output(60))
        }
    }
}