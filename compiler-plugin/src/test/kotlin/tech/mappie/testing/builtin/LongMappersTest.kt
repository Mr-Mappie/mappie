package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.math.BigInteger

class LongMappersTest {

    @TempDir
    lateinit var directory: File

    data class LongInput(val value: Long)

    data class BigIntegerOutput(val value: BigInteger)

    @Test
    fun `map Long to BigInteger implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.LongMappersTest.*

                        class Mapper : ObjectMappie<LongInput, BigIntegerOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Long = 2

            val mapper = classLoader
                .loadObjectMappieClass<LongInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
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
                        import tech.mappie.testing.builtin.LongMappersTest.*

                        class Mapper : ObjectMappie<LongInput, BigIntegerOutput>() {
                            override fun map(from: LongInput) = mapping {
                                to::value fromProperty from::value via LongToBigIntegerMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input: Long = 5

            val mapper = classLoader
                .loadObjectMappieClass<LongInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }
}