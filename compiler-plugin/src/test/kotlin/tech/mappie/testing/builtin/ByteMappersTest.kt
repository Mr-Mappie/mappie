package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger

class ByteMappersTest {

    @TempDir
    lateinit var directory: File

    data class ByteInput(val value: Byte)

    data class ShortOutput(val value: Short)

    data class IntOutput(val value: Int)

    data class LongOutput(val value: Long)

    data class BigIntegerOutput(val value: BigInteger)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Byte to Short implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, ShortOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, ShortOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(ShortOutput(input.toShort()))
        }
    }

    @Test
    fun `map Byte to Short explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, ShortOutput>() {
                    override fun map(from: ByteInput) = mapping {
                        to::value fromProperty from::value via ByteToShortMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, ShortOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(ShortOutput(input.toShort()))
        }
    }

    @Test
    fun `map Byte to Int implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, IntOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, IntOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(IntOutput(input.toInt()))
        }
    }

    @Test
    fun `map Byte to Int explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, IntOutput>() {
                    override fun map(from: ByteInput) = mapping {
                        to::value fromProperty from::value via ByteToIntMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, IntOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(IntOutput(input.toInt()))
        }
    }

    @Test
    fun `map Byte to Long implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, LongOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Byte to Long explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, LongOutput>() {
                    override fun map(from: ByteInput) = mapping {
                        to::value fromProperty from::value via ByteToLongMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, LongOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(LongOutput(input.toLong()))
        }
    }

    @Test
    fun `map Byte to BigInteger implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, BigIntegerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(BigIntegerOutput(BigInteger.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Byte to BigInteger explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, BigIntegerOutput>() {
                    override fun map(from: ByteInput) = mapping {
                        to::value fromProperty from::value via ByteToBigIntegerMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(BigIntegerOutput(BigInteger.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Byte to BigDecimal implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, BigDecimalOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(BigDecimalOutput(BigDecimal.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Byte to BigDecimal explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, BigDecimalOutput>() {
                    override fun map(from: ByteInput) = mapping {
                        to::value fromProperty from::value via ByteToBigDecimalMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(BigDecimalOutput(BigDecimal.valueOf(input.toLong())))
        }
    }

    @Test
    fun `map Byte to String implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 2

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Byte to String explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.ByteMappersTest.*

                class Mapper : ObjectMappie<ByteInput, StringOutput>() {
                    override fun map(from: ByteInput) = mapping {
                        to::value fromProperty from::value via ByteToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Byte = 5

            val mapper = classLoader
                .loadObjectMappieClass<ByteInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(ByteInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}