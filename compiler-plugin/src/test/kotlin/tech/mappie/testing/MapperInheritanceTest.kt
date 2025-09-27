package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapperInheritanceTest : MappieTestCase() {

    interface InputInterface { val id: String }
    data class Input(override val id: String) : InputInterface
    data class Output(val id: String)

    @Test
    fun `inherit from mapper should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.MapperInheritanceTest.*

                abstract class BaseMapper<FROM : InputInterface> : ObjectMappie<FROM, Output>()

                class Mapper : BaseMapper<Input>()
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input("test")))
                .isEqualTo(Output("test"))
        }
    }
}