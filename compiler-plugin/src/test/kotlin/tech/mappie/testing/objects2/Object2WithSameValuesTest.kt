package tech.mappie.testing.objects2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class Object2WithSameValuesTest : MappieTestCase() {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Output(val value: String, val age: Int)

    @Test
    fun `map two data classes into one should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects2.Object2WithSameValuesTest.*

                class Mapper : ObjectMappie2<Input1, Input2, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie2<Input1, Input2, Output>()

            assertThat(mapper.map(Input1("value"), Input2(10)))
                .isEqualTo(Output("value", 10))
        }
    }
}