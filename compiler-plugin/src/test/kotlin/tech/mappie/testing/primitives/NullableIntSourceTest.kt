package tech.mappie.testing.primitives

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.containsError
import java.io.File

@Suppress("unused")
class NullableIntSourceTest {
    data class Input(val value: Int?)
    data class LongOutput(val value: Long)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map int value to nullable long value should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.primitives.NullableIntSourceTest.*
    
                        class Mapper : ObjectMappie<Input, LongOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target LongOutput::value automatically resolved from Input::value but cannot assign source type Int? to target type Long")
        }
    }
}