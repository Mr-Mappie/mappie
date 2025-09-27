package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigDecimal
import java.math.BigInteger

class ShortMappersTest : MappieTestCase() {

    data class ShortInput(val value: Short)

    data class IntOutput(val value: Int)

    data class LongOutput(val value: Long)

    data class BigIntegerOutput(val value: BigInteger)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Short to Int implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, IntOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 2

            val mapper = objectMappie<ShortInput, IntOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(IntOutput(input.toInt()))
        }
    }

    @Test
    fun `map Short to Int explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, IntOutput>() {
                    override fun map(from: ShortInput) = mapping {
                        to::value fromProperty from::value via ShortToIntMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 5

            val mapper = objectMappie<ShortInput, IntOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(IntOutput(input.toInt()))
        }
    }

    @Test
    fun `map Short to Long implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.ShortMappersTest.*
    
                class Mapper : ObjectMappie<ShortInput, LongOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 2

            val mapper = objectMappie<ShortInput, LongOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Short to Long explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, LongOutput>() {
                    override fun map(from: ShortInput) = mapping {
                        to::value fromProperty from::value via ShortToLongMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 5

            val mapper = objectMappie<ShortInput, LongOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Short to BigInteger implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, BigIntegerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 2

            val mapper = objectMappie<ShortInput, BigIntegerOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(BigIntegerOutput(BigInteger.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Short to BigInteger explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, BigIntegerOutput>() {
                    override fun map(from: ShortInput) = mapping {
                        to::value fromProperty from::value via ShortToBigIntegerMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 5

            val mapper = objectMappie<ShortInput, BigIntegerOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(BigIntegerOutput(BigInteger.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Short to BigDecimal implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.ShortMappersTest.*

                        class Mapper : ObjectMappie<ShortInput, BigDecimalOutput>()
                        """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 2

            val mapper = objectMappie<ShortInput, BigDecimalOutput>()
            
            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(BigDecimalOutput(BigDecimal.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Short to BigDecimal explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, BigDecimalOutput>() {
                    override fun map(from: ShortInput) = mapping {
                        to::value fromProperty from::value via ShortToBigDecimalMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 5

            val mapper = objectMappie<ShortInput, BigDecimalOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(BigDecimalOutput(BigDecimal.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Short to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 2

            val mapper = objectMappie<ShortInput, StringOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Short to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.ShortMappersTest.*

                class Mapper : ObjectMappie<ShortInput, StringOutput>() {
                    override fun map(from: ShortInput) = mapping {
                        to::value fromProperty from::value via ShortToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Short = 5

            val mapper = objectMappie<ShortInput, StringOutput>()

            assertThat(mapper.map(ShortInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

}