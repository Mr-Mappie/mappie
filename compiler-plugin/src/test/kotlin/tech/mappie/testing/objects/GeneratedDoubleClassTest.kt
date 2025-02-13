package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GeneratedDoubleClassTest {
    data class Input(val a: InnerInput, val b: InnerInput)
    data class InnerInput(val value: String)
    data class Output(val a: InnerOutput, val b: InnerOutput)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested class without declaring mapping should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedDoubleClassTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerInput("a"), InnerInput("b"))))
                .isEqualTo(Output(InnerOutput("a"), InnerOutput("b")))
        }
    }
}