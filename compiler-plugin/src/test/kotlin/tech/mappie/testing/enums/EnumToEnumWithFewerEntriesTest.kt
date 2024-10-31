package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadEnumMappieClass
import java.io.File
import kotlin.test.Test

class EnumToEnumWithFewerEntriesTest {

    enum class Input { FIRST, SECOND, THIRD }
    enum class Output { FIRST, SECOND }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map enums with different entries with explicit fromEnumEntry should succeed`() {
        compile(directory) {
            file(
                "Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*

                class Mapper : EnumMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output.FIRST fromEnumEntry Input.THIRD
                    }
                }
                """
            )
        } satisfies  {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadEnumMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThat(mapper.map(Input.THIRD)).isEqualTo(Output.FIRST)
        }
    }

    @Test
    fun `map enums with the different entries explicit thrownByEnumEntry should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*

                class Mapper : EnumMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        kotlin.IllegalStateException() thrownByEnumEntry Input.THIRD
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadEnumMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThatThrownBy { mapper.map(Input.THIRD) }.isInstanceOf(IllegalStateException::class.java)
        }
    }

    @Test
    fun `map enums with the different entries should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*

                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(4, "Source Input.THIRD has no target defined")
        }
    }

    @Test
    fun `map enums with the different entries with strict enums enabled should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*
                import tech.mappie.api.config.UseStrictEnums

                @UseStrictEnums
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(5, "Source Input.THIRD has no target defined")
        }
    }

    @Test
    fun `map enums with the different entries with strict enums disabled should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumToEnumWithFewerEntriesTest.*
                import tech.mappie.api.config.UseStrictEnums

                @UseStrictEnums(false)
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies  {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadEnumMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThrows<NoWhenBranchMatchedException> { mapper.map(Input.THIRD) }
        }
    }
}
