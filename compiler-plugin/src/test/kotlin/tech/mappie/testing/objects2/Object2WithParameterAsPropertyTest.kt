package tech.mappie.testing.objects2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappie2Class
import java.io.File

class Object2WithParameterAsPropertyTest {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Output(val value: String, val input: Input2)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical data classes should succeed`() {
        compile(directory) {
            file("Test.kt",
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
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappie2Class<Input1, Input2, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input1("value"), Input2(10))).isEqualTo(Output("value", Input2(10)))
        }
    }
}