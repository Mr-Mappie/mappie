package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigDecimal
import java.math.BigInteger

class LongMappersTest : MappieTestCase() {

    data class LongInput(val value: Long)

    data class BigIntegerOutput(val value: BigInteger)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Long to BigInteger implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.LongMappersTest.*

                class Mapper : ObjectMappie<LongInput, BigIntegerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Long = 2

            val mapper = objectMappie<LongInput, BigIntegerOutput>()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Long to BigInteger explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.api.builtin.LongMappersTest.*

                        class Mapper : ObjectMappie<LongInput, BigIntegerOutput>() {
                            override fun map(from: LongInput) = mapping {
                                to::value fromProperty from::value via LongToBigIntegerMapper()
                            }
                        }
                        """
            )
        } satisfies {
           isOk()
            hasNoWarningsOrErrors()

            val input: Long = 5

            val mapper = objectMappie<LongInput, BigIntegerOutput>()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Long to BigDecimal implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.LongMappersTest.*

                        class Mapper : ObjectMappie<LongInput, BigDecimalOutput>()
                        """
            )
        } satisfies {
           isOk()
            hasNoWarningsOrErrors()

            val input: Long = 2

            val mapper = objectMappie<LongInput, BigDecimalOutput>()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Long to BigDecimal explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.api.builtin.LongMappersTest.*

                        class Mapper : ObjectMappie<LongInput, BigDecimalOutput>() {
                            override fun map(from: LongInput) = mapping {
                                to::value fromProperty from::value via LongToBigDecimalMapper()
                            }
                        }
                        """
            )
        } satisfies {
           isOk()
            hasNoWarningsOrErrors()

            val input: Long = 5

            val mapper = objectMappie<LongInput, BigDecimalOutput>()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Long to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.LongMappersTest.*

                class Mapper : ObjectMappie<LongInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Long = 2

            val mapper = objectMappie<LongInput, StringOutput>()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Long to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.api.builtin.LongMappersTest.*

                        class Mapper : ObjectMappie<LongInput, StringOutput>() {
                            override fun map(from: LongInput) = mapping {
                                to::value fromProperty from::value via LongToStringMapper()
                            }
                        }
                        """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Long = 5

            val mapper = objectMappie<LongInput, StringOutput>()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

}