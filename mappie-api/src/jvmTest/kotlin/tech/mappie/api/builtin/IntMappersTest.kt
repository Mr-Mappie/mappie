package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigDecimal
import java.math.BigInteger

class IntMappersTest : MappieTestCase() {

    data class IntInput(val value: Int)

    data class LongOutput(val value: Long)

    data class BigIntegerOutput(val value: BigInteger)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Int to Long implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, LongOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2

            val mapper = objectMappie<IntInput, LongOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Int to Long explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, LongOutput>() {
                    override fun map(from: IntInput) = mapping {
                        to::value fromProperty from::value via IntToLongMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 5

            val mapper = objectMappie<IntInput, LongOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Int to BigInteger implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, BigIntegerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2

            val mapper = objectMappie<IntInput, BigIntegerOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Int to BigInteger explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, BigIntegerOutput>() {
                    override fun map(from: IntInput) = mapping {
                        to::value fromProperty from::value via IntToBigIntegerMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 5

            val mapper = objectMappie<IntInput, BigIntegerOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Int to BigDecimal implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, BigDecimalOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2

            val mapper = objectMappie<IntInput, BigDecimalOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Int to BigDecimal explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, BigDecimalOutput>() {
                    override fun map(from: IntInput) = mapping {
                        to::value fromProperty from::value via IntToBigDecimalMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 5

            val mapper = objectMappie<IntInput, BigDecimalOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Int to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 2

            val mapper = objectMappie<IntInput, StringOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Int to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.IntMappersTest.*

                class Mapper : ObjectMappie<IntInput, StringOutput>() {
                    override fun map(from: IntInput) = mapping {
                        to::value fromProperty from::value via IntToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 5

            val mapper = objectMappie<IntInput, StringOutput>()

            assertThat(mapper.map(IntInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}