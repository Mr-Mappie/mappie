package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadEnumMappieClass
import java.io.File
import kotlin.test.Test

class EnumToEnumWithSameEntriesTest {

    enum class Input { SOME, OTHER }
    enum class Output { SOME, OTHER }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical enums should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadEnumMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            Input.entries.forEach { entry ->
                assertThat(mapper.map(entry))
                    .isEqualTo(Output.valueOf(entry.name))
            }
        }
    }

    @Test
    fun `map identical enums with an explicit mapping should warn`() {
        compile(directory) {
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
            hasWarningMessage(6, "Unnecessary explicit mapping of source Input.SOME")

            val mapper = classLoader
                .loadEnumMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            Input.entries.forEach { entry ->
                assertThat(mapper.map(entry)).isEqualTo(Output.valueOf(entry.name))
            }
        }
    }

    @Test
    fun `map identical enums with an explicit mapping should not warn when suppressed`() {
        compile(directory) {
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
