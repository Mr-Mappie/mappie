package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class MapperInheritanceTest {

    interface InputInterface { val id: String }
    data class Input(override val id: String) : InputInterface
    data class Output(val id: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `inherit from mapper should succeed`() {
        compile(directory) {
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
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("test")))
                .isEqualTo(Output("test"))
        }
    }
}