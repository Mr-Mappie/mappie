package tech.mappie.testing.primitives

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.containsError
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

@Suppress("unused")
class IntTest {
    data class Input(val value: Int)
    data class ShortOutput(val value: Short)
    data class IntOutput(val value: Int)
    data class LongOutput(val value: Long)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map int value to short value should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.primitives.IntTest.*
    
                        class Mapper : ObjectMappie<Input, ShortOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages).containsError("Target ShortOutput.value has type Short which cannot be assigned from type Int")
        }
    }

    @Test
    fun `map int value to int value should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.primitives.IntTest.*
    
                        class Mapper : ObjectMappie<Input, IntOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadObjectMappieClass<Input, IntOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(1))).isEqualTo(IntOutput(1))
        }
    }

    @Test
    fun `map int value to long value should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.primitives.IntTest.*
    
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
        }
    }
}