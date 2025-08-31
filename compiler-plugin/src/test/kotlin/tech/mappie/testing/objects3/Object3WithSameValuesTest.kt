package tech.mappie.testing.objects3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class Object3WithSameValuesTest : MappieTestCase() {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Input3(val char: Char)
    data class Output(val value: String, val age: Int, val char: Char)

    @Test
    fun `map three data classes into one should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie3
                import tech.mappie.testing.objects3.Object3WithSameValuesTest.*

                class Mapper : ObjectMappie3<Input1, Input2, Input3, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie3<Input1, Input2, Input3, Output>()

            assertThat(mapper.map(Input1("value"), Input2(10), Input3('c')))
                .isEqualTo(Output("value", 10, 'c'))
        }
    }
}