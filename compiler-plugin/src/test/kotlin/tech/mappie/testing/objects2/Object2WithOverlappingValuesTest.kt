package tech.mappie.testing.objects2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class Object2WithOverlappingValuesTest : MappieTestCase() {

    data class Input1(val value: String, val age: Int)
    data class Input2(val age: Int)
    data class Output(val value: String, val age: Int)

    @Test
    fun `map duplicate input property should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects2.Object2WithOverlappingValuesTest.*

                class Mapper : ObjectMappie2<Input1, Input2, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "Target Output::age has multiple sources defined: first::age, second::age")
        }
    }

    @Test
    fun `map duplicate input property with one specified should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects2.Object2WithOverlappingValuesTest.*

                class Mapper : ObjectMappie2<Input1, Input2, Output>() {
                    override fun map(first: Input1, second: Input2) = mapping {
                        to::age fromProperty second::age
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie2<Input1, Input2, Output>()

            assertThat(mapper.map(Input1("value", 10), Input2(20)))
                .isEqualTo(Output("value", 20)
            )
        }
    }
}