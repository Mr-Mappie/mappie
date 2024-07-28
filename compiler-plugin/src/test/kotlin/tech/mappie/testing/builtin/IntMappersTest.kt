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

class IntMappersTest {

    @TempDir
    lateinit var directory: File

    data class IntInput(val value: Int)

    data class LongOutput(val value: Long)

    data class BigIntegerOutput(val value: BigInteger)

    data class BigDecimalOutput(val value: BigDecimal)

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

    @Test
    fun `map Int to BigInteger implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.IntMappersTest.*

                        class Mapper : ObjectMappie<IntInput, BigIntegerOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 2

            val mapper = classLoader
                .loadObjectMappieClass<IntInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Int to BigInteger explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.IntMappersTest.*

                        class Mapper : ObjectMappie<IntInput, BigIntegerOutput>() {
                            override fun map(from: IntInput) = mapping {
                                to::value fromProperty from::value via IntToBigIntegerMapper()
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
                .loadObjectMappieClass<IntInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Int to BigDecimal implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.IntMappersTest.*

                        class Mapper : ObjectMappie<IntInput, BigDecimalOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 2

            val mapper = classLoader
                .loadObjectMappieClass<IntInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Int to BigDecimal explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.IntMappersTest.*

                        class Mapper : ObjectMappie<IntInput, BigDecimalOutput>() {
                            override fun map(from: IntInput) = mapping {
                                to::value fromProperty from::value via IntToBigDecimalMapper()
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
                .loadObjectMappieClass<IntInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }
}