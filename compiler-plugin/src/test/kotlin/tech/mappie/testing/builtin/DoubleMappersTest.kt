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

class DoubleMappersTest {

    @TempDir
    lateinit var directory: File

    data class DoubleInput(val value: Double)

    data class BigDecimalOutput(val value: BigDecimal)

    @Test
    fun `map Double to BigDecimal implicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.DoubleMappersTest.*

                        class Mapper : ObjectMappie<DoubleInput, BigDecimalOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 2.0

            val mapper = classLoader
                .loadObjectMappieClass<DoubleInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Double to BigDecimal explicit should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.DoubleMappersTest.*

                        class Mapper : ObjectMappie<DoubleInput, BigDecimalOutput>() {
                            override fun map(from: DoubleInput) = mapping {
                                to::value fromProperty from::value via DoubleToBigDecimalMapper()
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val input = 5.0

            val mapper = classLoader
                .loadObjectMappieClass<DoubleInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }
}