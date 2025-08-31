package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigInteger

class BigIntegerMappersTest : MappieTestCase() {
    
    data class BigIntegerInput(val value: BigInteger)

    data class StringOutput(val value: String)

    @Test
    fun `map BigInteger to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.BigIntegerMappersTest.*

                class Mapper : ObjectMappie<BigIntegerInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = BigInteger.valueOf(10)

            val mapper = objectMappie<BigIntegerInput, StringOutput>()

            assertThat(mapper.map(BigIntegerInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map BigInteger to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.BigIntegerMappersTest.*

                class Mapper : ObjectMappie<BigIntegerInput, StringOutput>() {
                    override fun map(from: BigIntegerInput) = mapping {
                        to::value fromProperty from::value via BigIntegerToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = BigInteger.valueOf(100)

            val mapper = objectMappie<BigIntegerInput, StringOutput>()

            assertThat(mapper.map(BigIntegerInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}