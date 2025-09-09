package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigDecimal

class DoubleMappersTest : MappieTestCase() {
    
    data class DoubleInput(val value: Double)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Double to BigDecimal implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.DoubleMappersTest.*

                class Mapper : ObjectMappie<DoubleInput, BigDecimalOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2.0

            val mapper = objectMappie<DoubleInput, BigDecimalOutput>()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Double to BigDecimal explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.DoubleMappersTest.*
    
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

            val mapper = objectMappie<DoubleInput, BigDecimalOutput>()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Double to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.DoubleMappersTest.*

                class Mapper : ObjectMappie<DoubleInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2.0

            val mapper = objectMappie<DoubleInput, StringOutput>()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Double to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.DoubleMappersTest.*

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

            val mapper = objectMappie<DoubleInput, StringOutput>()

            assertThat(mapper.map(DoubleInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}