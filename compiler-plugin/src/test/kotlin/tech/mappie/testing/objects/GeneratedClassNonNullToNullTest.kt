package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GeneratedClassNonNullToNullTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: String)
    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: String?)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested non-nullable to nullable without declaring mapping should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassNonNullToNullTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerInput("value"))))
                .isEqualTo(Output(InnerOutput("value")))
        }
    }
}