package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.assertThrows
import tech.mappie.testing.MappieTestCase
import kotlin.test.Test

class EnumToEnumWithFewerEntriesTest : MappieTestCase() {

    enum class Input { FIRST, SECOND, THIRD, FOURTH }
    enum class Output { FIRST, SECOND }

    @Test
    fun `map enums with different entries with explicit fromEnumEntry should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*

                class Mapper : EnumMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output.FIRST fromEnumEntry Input.THIRD
                        Output.FIRST fromEnumEntry Input.FOURTH
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = enumMappie<Input, Output>()

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThat(mapper.map(Input.THIRD)).isEqualTo(Output.FIRST)
        }
    }

    @Test
    fun `map enums with the different entries explicit thrownByEnumEntry should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*

                class Mapper : EnumMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        kotlin.IllegalStateException() thrownByEnumEntry Input.THIRD
                        kotlin.IllegalStateException() thrownByEnumEntry Input.FOURTH
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = enumMappie<Input, Output>()

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThatThrownBy { mapper.map(Input.THIRD) }.isInstanceOf(IllegalStateException::class.java)
            assertThatThrownBy { mapper.map(Input.FOURTH) }.isInstanceOf(IllegalStateException::class.java)
        }
    }

    @Test
    fun `map enums with the different entries should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*

                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasSingleErrorMessage(4, "Source(s) 'Input.THIRD' and 'Input.FOURTH' has/have no target defined.")
        }
    }

    @Test
    fun `map enums with the same target set twice should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*

                class Mapper : EnumMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output.FIRST fromEnumEntry Input.THIRD
                        Output.FIRST fromEnumEntry Input.FOURTH
                        Output.FIRST fromEnumEntry Input.FOURTH
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasSingleErrorMessage(5, "Source(s) 'Input.FOURTH' has/have multiple targets defined.")
        }
    }

    @Test
    fun `map enums with the different entries with strict enums enabled should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*
                import tech.mappie.api.config.UseStrictEnums

                @UseStrictEnums
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasSingleErrorMessage(5, "Source(s) 'Input.THIRD' and 'Input.FOURTH' has/have no target defined.")
        }
    }

    @Test
    fun `map enums with the different entries with strict enums disabled should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*
                import tech.mappie.api.config.UseStrictEnums

                @UseStrictEnums(false)
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = enumMappie<Input, Output>()

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThrows<NoWhenBranchMatchedException> { mapper.map(Input.THIRD) }
        }
    }
}
