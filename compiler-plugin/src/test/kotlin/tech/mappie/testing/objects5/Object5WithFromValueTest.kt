package tech.mappie.testing.objects5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class Object5WithFromValueTest : MappieTestCase() {

    data class Input5(val float: Float)
    data class Output(val first: Long, val second: Long, val third: Long, val fourth: String, val float: Float)

    @Test
    fun `map five data classes into one should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.Object5WithFromValueTest.*

                class Mapper : ObjectMappie5<Long, Long, Long, String, Input5, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie5<Long, Long, Long, String, Input5, Output>()

            assertThat(mapper.map(1, 2, 3, "value", Input5(1.0f)))
                .isEqualTo(Output(1, 2, 3, "value", 1.0f))
        }
    }

    @Test
    fun `map five data classes into one with run should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie5
                import tech.mappie.testing.objects5.Object5WithFromValueTest.*

                class Mapper : ObjectMappie5<Long, Long, Long, String, Input5, Output>() {
                    override fun map(first: Long, second: Long, third: Long, fourth: String, fifth: Input5) = mapping {
                        to::fourth fromValue run { uppercase("test") }
                        to::float fromProperty fifth::float transform { it + 1 }
                    }
                }

                private fun uppercase(value: String) = value.uppercase()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6,
            "The function 'run' was called as an extension method on the mapping dsl which does not exist after compilation")
        }
    }
}
