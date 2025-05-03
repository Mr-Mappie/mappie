package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.math.BigDecimal

class FloatMappersTest {

    @TempDir
    lateinit var directory: File

    data class FloatInput(val value: Float)

    data class DoubleOutput(val value: Double)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Float to Double implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.FloatMappersTest.*

                class Mapper : ObjectMappie<FloatInput, DoubleOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

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
        compile(directory) {
            file("Test.kt",
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
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

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
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.FloatMappersTest.*

                class Mapper : ObjectMappie<FloatInput, BigDecimalOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

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
        compile(directory) {
            file("Test.kt",
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
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

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

    @Test
    fun `map Float to String implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.FloatMappersTest.*

                class Mapper : ObjectMappie<FloatInput, StringOutput>()
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2.0f

            val mapper = classLoader
                .loadObjectMappieClass<FloatInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(FloatInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Float to String explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.FloatMappersTest.*

                class Mapper : ObjectMappie<FloatInput, StringOutput>() {
                    override fun map(from: FloatInput) = mapping {
                        to::value fromProperty from::value via FloatToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 5.0f

            val mapper = classLoader
                .loadObjectMappieClass<FloatInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(FloatInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}