package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.math.BigDecimal

class BigDecimalMappersTest {

    @TempDir
    lateinit var directory: File

    data class BigDecimalInput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map BigDecimal to String implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.BigDecimalMappersTest.*

                        class Mapper : ObjectMappie<BigDecimalInput, StringOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = BigDecimal.valueOf(10)

            val mapper = classLoader
                .loadObjectMappieClass<BigDecimalInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(BigDecimalInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map BigDecimal to String explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.BigDecimalMappersTest.*

                        class Mapper : ObjectMappie<BigDecimalInput, StringOutput>() {
                            override fun map(from: BigDecimalInput) = mapping {
                                to::value fromProperty from::value via BigDecimalToStringMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = BigDecimal.valueOf(100)

            val mapper = classLoader
                .loadObjectMappieClass<BigDecimalInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(BigDecimalInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}
