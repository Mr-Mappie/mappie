package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.math.BigDecimal

class DoubleMappersTest {

    @TempDir
    lateinit var directory: File

    data class DoubleInput(val value: Double)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Double to BigDecimal implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.DoubleMappersTest.*

                class Mapper : ObjectMappie<DoubleInput, BigDecimalOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

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
        compile(directory) {
            file("Test.kt",
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
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

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

    @Test
    fun `map Double to String implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.DoubleMappersTest.*

                class Mapper : ObjectMappie<DoubleInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2.0

            val mapper = classLoader
                .loadObjectMappieClass<DoubleInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Double to String explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.DoubleMappersTest.*

                class Mapper : ObjectMappie<DoubleInput, StringOutput>() {
                    override fun map(from: DoubleInput) = mapping {
                        to::value fromProperty from::value via DoubleToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 5.0

            val mapper = classLoader
                .loadObjectMappieClass<DoubleInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}