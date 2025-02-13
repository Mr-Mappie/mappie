package tech.mappie.testing.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class CharMappersTest {

    @TempDir
    lateinit var directory: File

    data class CharInput(val value: Char)

    data class StringOutput(val value: String)

    @Test
    fun `map Char to String implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.builtin.CharMappersTest.*

                class Mapper : ObjectMappie<CharInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val input = 'b'

            val mapper = classLoader
                .loadObjectMappieClass<CharInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(CharInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Char to String explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.testing.builtin.CharMappersTest.*

                class Mapper : ObjectMappie<CharInput, StringOutput>() {
                    override fun map(from: CharInput) = mapping {
                        to::value fromProperty from::value via CharToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val input = 'b'

            val mapper = classLoader
                .loadObjectMappieClass<CharInput, StringOutput>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(CharInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}
