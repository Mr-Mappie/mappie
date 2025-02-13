package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class GeneratedClassFailTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: String)
    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested class without declaring mapping should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassFailTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(
                4,
                "No implicit mapping can be generated from InnerInput to InnerOutput",
                listOf(
                    "Target InnerOutput::value automatically resolved from InnerInput::value but cannot assign source type String to target type Int"
                )
            )
        }
    }
}