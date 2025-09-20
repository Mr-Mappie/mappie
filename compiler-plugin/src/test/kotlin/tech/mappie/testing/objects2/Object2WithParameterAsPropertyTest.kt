package tech.mappie.testing.objects2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class Object2WithParameterAsPropertyTest : MappieTestCase() {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Output(val value: String, val input: Input2)

    @Test
    fun `map parameter itself into output should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects2.Object2WithParameterAsPropertyTest.*

                class Mapper : ObjectMappie2<Input1, Input2, Output>() {
                    override fun map(source: Input1, input: Input2) = mapping {
                        
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie2<Input1, Input2, Output>()

            assertThat(mapper.map(Input1("value"), Input2(10)))
                .isEqualTo(Output("value", Input2(10)))
        }
    }
}