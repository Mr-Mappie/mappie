package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger

class LongMappersTest {

    @TempDir
    lateinit var directory: File

    data class LongInput(val value: Long)

    data class BigIntegerOutput(val value: BigInteger)

    data class BigDecimalOutput(val value: BigDecimal)

    data class StringOutput(val value: String)

    @Test
    fun `map Long to BigInteger implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.LongMappersTest.*

                class Mapper : ObjectMappie<LongInput, BigIntegerOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input: Long = 2

            val mapper = classLoader
                .loadObjectMappieClass<LongInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Long to BigInteger explicit should succeed`() {
        compile(directory) {
                    file("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.LongMappersTest.*

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

            val mapper = classLoader
                .loadObjectMappieClass<LongInput, BigIntegerOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigIntegerOutput(input.toBigInteger()))
        }
    }

    @Test
    fun `map Long to BigDecimal implicit should succeed`() {
        compile(directory) {
                    file("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.builtin.LongMappersTest.*

                        class Mapper : ObjectMappie<LongInput, BigDecimalOutput>()
                        """
                    )        
           } satisfies {
           isOk()
            hasNoWarningsOrErrors()

            val input: Long = 2

            val mapper = classLoader
                .loadObjectMappieClass<LongInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }

    @Test
    fun `map Long to BigDecimal explicit should succeed`() {
        compile(directory) {
                    file("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.api.builtin.*
                        import tech.mappie.testing.builtin.LongMappersTest.*

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

            val mapper = classLoader
                .loadObjectMappieClass<LongInput, BigDecimalOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(LongInput(input)))
                .isEqualTo(BigDecimalOutput(input.toBigDecimal()))
        }
    }
}