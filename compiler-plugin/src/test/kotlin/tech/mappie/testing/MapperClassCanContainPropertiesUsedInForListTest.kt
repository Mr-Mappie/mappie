package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperClassCanContainPropertiesUsedInForListTest : MappieTestCase() {

    data class Input(val text: List<InnerInput>)
    data class InnerInput(val text: String)

    data class Output(val text: List<InnerOutput>)
    data class InnerOutput(val text: String, val int: Int)

    @Test
    fun `map a property in constructor of mapper should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassCanContainPropertiesUsedInForListTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::text fromProperty from::text via InnerMapper(10).forList
                    }
                }
                
                class InnerMapper(private val int: Int): ObjectMappie<InnerInput, InnerOutput>() {
                    override fun map(from: InnerInput) = mapping { 
                        InnerOutput::int fromValue int
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input(listOf(InnerInput("test")))))
                .isEqualTo(Output(listOf(InnerOutput("test", 10))))
        }
    }
}