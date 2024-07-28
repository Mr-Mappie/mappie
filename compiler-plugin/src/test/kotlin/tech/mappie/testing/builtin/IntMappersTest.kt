package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class IntMappersTest {

    @TempDir
    lateinit var directory: File

    data class IntInput(val value: Int)

    data class LongOutput(val value: Long)

    @Test
    fun `map Int to Long implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.IntMappersTest.*

                        class Mapper : ObjectMappie<IntInput, LongOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 2

            val mapper = classLoader
                .loadObjectMappieClass<IntInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Int to Long explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.IntMappersTest.*

                        class Mapper : ObjectMappie<IntInput, LongOutput>() {
                            override fun map(from: IntInput) = mapping {
                                to::value fromProperty from::value via IntToLongMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 5

            val mapper = classLoader
                .loadObjectMappieClass<IntInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }
}