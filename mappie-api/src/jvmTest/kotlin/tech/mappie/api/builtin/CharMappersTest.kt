package tech.mappie.api.builtin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class CharMappersTest : MappieTestCase() {

    data class CharInput(val value: Char)

    data class StringOutput(val value: String)

    @Test
    fun `map Char to String implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.CharMappersTest.*

                class Mapper : ObjectMappie<CharInput, StringOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 'b'

            val mapper = objectMappie<CharInput, StringOutput>()

            assertThat(mapper.map(CharInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }

    @Test
    fun `map Char to String explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.*
                import tech.mappie.api.builtin.CharMappersTest.*

                class Mapper : ObjectMappie<CharInput, StringOutput>() {
                    override fun map(from: CharInput) = mapping {
                        to::value fromProperty from::value via CharToStringMapper()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val input = 'b'

            val mapper = objectMappie<CharInput, StringOutput>()

            assertThat(mapper.map(CharInput(input)))
                .isEqualTo(StringOutput(input.toString()))
        }
    }
}