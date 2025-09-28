package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigDecimal

class BigDecimalMappersTest : MappieTestCase() {

    data class BigDecimalInput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map BigDecimal to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.BigDecimalMappersTest.*

                class Mapper : ObjectMappie<BigDecimalInput, StringOutput>()
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val input = BigDecimal.valueOf(10)

            val mapper = objectMappie<BigDecimalInput, StringOutput>()

            assertThat(mapper.map(BigDecimalInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map BigDecimal to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.BigDecimalMappersTest.*

                class Mapper : ObjectMappie<BigDecimalInput, StringOutput>() {
                    override fun map(from: BigDecimalInput) = mapping {
                        to::value fromProperty from::value via BigDecimalToStringMapper()
                    }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val input = BigDecimal.valueOf(100)

            val mapper = objectMappie<BigDecimalInput, StringOutput>()

            assertThat(mapper.map(BigDecimalInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}