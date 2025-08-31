package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import tech.mappie.testing.MappieTestCase
import kotlin.test.Test

class EnumToExpressionTest : MappieTestCase() {

    enum class Input { TRUE, FALSE }

    @Test
    fun `map enum to expression should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToExpressionTest.*

                class Mapper : EnumMappie<Input, Boolean>() {
                    override fun map(from: Input) = mapping {
                        true fromEnumEntry Input.TRUE
                        false fromEnumEntry Input.FALSE
                    }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = enumMappie<Input, Boolean>()

            assertThat(mapper.map(Input.TRUE)).isEqualTo(true)
            assertThat(mapper.map(Input.FALSE)).isEqualTo(false)
        }
    }
}
