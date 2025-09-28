package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import tech.mappie.testing.MappieTestCase
import kotlin.test.Test

class EnumToEnumWithSameEntriesTest : MappieTestCase() {

    enum class Input { SOME, OTHER }
    enum class Output { SOME, OTHER }

    @Test
    fun `map identical enums should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithSameEntriesTest.*

                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies  {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = enumMappie<Input, Output>()

            Input.entries.forEach { entry ->
                assertThat(mapper.map(entry))
                    .isEqualTo(Output.valueOf(entry.name))
            }
        }
    }

    @Test
    fun `map identical enums with an explicit mapping should warn`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithSameEntriesTest.*

                class Mapper : EnumMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output.SOME fromEnumEntry Input.SOME
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(6, "Unnecessary explicit mapping of source 'Input.SOME'")

            val mapper = enumMappie<Input, Output>()

            Input.entries.forEach { entry ->
                assertThat(mapper.map(entry)).isEqualTo(Output.valueOf(entry.name))
            }
        }
    }

    @Test
    fun `map identical enums with an explicit mapping should not warn when suppressed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithSameEntriesTest.*

                @Suppress("UNNECESSARY_EXPLICIT_MAPPING")
                class Mapper : EnumMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output.SOME fromEnumEntry Input.SOME
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }
}
