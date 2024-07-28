package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class CharMappersTest {

    @TempDir
    lateinit var directory: File

    data class CharInput(val value: Char)

    data class StringOutput(val value: String)

    @Test
    fun `map Char to String implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.CharMappersTest.*

                        class Mapper : ObjectMappie<CharInput, StringOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 'b'

            val mapper = classLoader
                .loadObjectMappieClass<CharInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(CharInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Long to BigInteger explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.CharMappersTest.*

                        class Mapper : ObjectMappie<CharInput, StringOutput>() {
                            override fun map(from: CharInput) = mapping {
                                to::value fromProperty from::value via CharToStringMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 'b'

            val mapper = classLoader
                .loadObjectMappieClass<CharInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(CharInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}
