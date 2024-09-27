package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File
import java.math.BigInteger

class BigIntegerMappersTest {

    @TempDir
    lateinit var directory: File

    data class BigIntegerInput(val value: BigInteger)

    data class StringOutput(val value: String)

    @Test
    fun `map BigInteger to String implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.BigIntegerMappersTest.*

                class Mapper : ObjectMappie<BigIntegerInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val input = BigInteger.valueOf(10)

            val mapper = classLoader
                .loadObjectMappieClass<BigIntegerInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(BigIntegerInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map BigInteger to String explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.BigIntegerMappersTest.*

                class Mapper : ObjectMappie<BigIntegerInput, StringOutput>() {
                    override fun map(from: BigIntegerInput) = mapping {
                        to::value fromProperty from::value via BigIntegerToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val input = BigInteger.valueOf(100)

            val mapper = classLoader
                .loadObjectMappieClass<BigIntegerInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(BigIntegerInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}
