package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperClassInsideOtherDeclarationTest : MappieTestCase() {

    data class Input(val text: String)
    data class Output(val text: String)

    @Test
    fun `mapper can be declared inside an arbitrary class`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassInsideOtherDeclarationTest.*

                class Thing {
                    class Mapper : ObjectMappie<Input, Output>()                            
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>($$"Thing$Mapper").map(Input("test")))
                .isEqualTo(Output("test"))
        }
    }
}