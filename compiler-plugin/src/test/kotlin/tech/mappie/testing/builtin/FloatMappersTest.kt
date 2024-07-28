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
import java.math.BigInteger

class FloatMappersTest {

    @TempDir
    lateinit var directory: File

    data class FloatInput(val value: Float)

    data class DoubleOutput(val value: Double)

    data class BigDecimalOutput(val value: BigDecimal)

    @Test
    fun `map Float to Double implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.FloatMappersTest.*

                        class Mapper : ObjectMappie<FloatInput, DoubleOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 2.0f

            val mapper = classLoader
                .loadObjectMappieClass<FloatInput, DoubleOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(FloatInput(input)))
                .isEqualTo(DoubleOutput(input.toDouble()))
        }
    }

    @Test
    fun `map Float to Double explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.FloatMappersTest.*

                        class Mapper : ObjectMappie<FloatInput, DoubleOutput>() {
                            override fun map(from: FloatInput) = mapping {
                                to::value fromProperty from::value via FloatToDoubleMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 5.0f

            val mapper = classLoader
                .loadObjectMappieClass<FloatInput, DoubleOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(FloatInput(input)))
                .isEqualTo(DoubleOutput(input.toDouble()))
        }
    }

    @Test
    fun `map Float to BigDecimal implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.FloatMappersTest.*

                        class Mapper : ObjectMappie<FloatInput, BigDecimalOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 2.0f

            val mapper = classLoader
                .loadObjectMappieClass<FloatInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(FloatInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Float to BigDecimal explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.FloatMappersTest.*

                        class Mapper : ObjectMappie<FloatInput, BigDecimalOutput>() {
                            override fun map(from: FloatInput) = mapping {
                                to::value fromProperty from::value via FloatToBigDecimalMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 5.0f

            val mapper = classLoader
                .loadObjectMappieClass<FloatInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(FloatInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }
}