package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperClassFromDifferentFileTest : MappieTestCase() {

    data class Input(val inner: InnerInput)
    data class InnerInput(val string: String)
    
    data class Output(val inner: InnerOutput)
    data class InnerOutput(val string: String)

    @Test
    fun `mapper class from different file should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassFromDifferentFileTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )

            file("InnerMapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperClassFromDifferentFileTest.*

                class InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input(InnerInput("test"))))
                .isEqualTo(Output(InnerOutput("test")))
        }
    }
}