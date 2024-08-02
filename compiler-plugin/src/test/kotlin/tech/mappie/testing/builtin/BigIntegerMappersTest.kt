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

class BigIntegerMappersTest {

    @TempDir
    lateinit var directory: File

    data class BigIntegerInput(val value: BigInteger)

    data class StringOutput(val value: String)

    @Test
    fun `map BigInteger to String implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.BigIntegerMappersTest.*

                        class Mapper : ObjectMappie<BigIntegerInput, StringOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = BigInteger.valueOf(10)

            val mapper = classLoader
                .loadObjectMappieClass<BigIntegerInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(BigIntegerInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map BigInteger to String explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.BigIntegerMappersTest.*

                        class Mapper : ObjectMappie<BigIntegerInput, StringOutput>() {
                            override fun map(from: BigIntegerInput) = mapping {
                                to::value fromProperty from::value via BigIntegerToStringMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = BigInteger.valueOf(100)

            val mapper = classLoader
                .loadObjectMappieClass<BigIntegerInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(BigIntegerInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}
