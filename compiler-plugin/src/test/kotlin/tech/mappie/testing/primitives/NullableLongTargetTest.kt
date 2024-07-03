package tech.mappie.testing.primitives

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

@Suppress("unused")
class NullableLongTargetTest {
    data class Input(val value: Int)
    data class LongOutput(val value: Long?)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map int value to nullable long value should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.primitives.NullableLongTargetTest.*
    
                        class Mapper : ObjectMappie<Input, LongOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadObjectMappieClass<Input, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(1))).isEqualTo(LongOutput(1L))
            assertThat(mapper.map(Input(1))).isEqualTo(LongOutput(1L))
        }
    }
}