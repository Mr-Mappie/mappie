package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.containsError
import java.io.File

class GeneratedClassFailTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: String)
    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested class without declaring mapping should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.GeneratedClassFailTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages).containsError(
                "No implicit mapping can be generated from InnerInput to InnerOutput",
                listOf(
                    "Target InnerOutput::value automatically resolved from InnerInput::value but cannot assign source type String to target type Int"
                )
            )
        }
    }
}