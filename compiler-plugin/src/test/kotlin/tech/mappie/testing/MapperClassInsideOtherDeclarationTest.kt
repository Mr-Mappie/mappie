package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile

import java.io.File

class MapperClassInsideOtherDeclarationTest {

    data class Input(val text: String)
    data class Output(val text: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapper can be declared inside an arbitrary class`() {
        compile(directory) {
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
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Thing\$Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("test")))
                .isEqualTo(Output("test"))
        }
    }
}