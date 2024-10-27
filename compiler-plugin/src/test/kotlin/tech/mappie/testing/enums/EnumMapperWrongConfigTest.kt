package tech.mappie.testing.enums

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class EnumMapperWrongConfigTest {

    enum class Input { A, B }
    enum class Output { A, B }

    @TempDir
    lateinit var directory: File

    @Test
    fun `declaring an EnumMappie with @UseDefaultArguments should warn`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumMapperWrongConfigTest.*
                import tech.mappie.api.config.UseDefaultArguments

                @UseDefaultArguments
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(5, "Annotation @UseDefaultArguments has no effect on children of EnumMappie")
        }
    }

    @Test
    fun `declaring an EnumMappie with @UseStrictVisibility should warn`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumMapperWrongConfigTest.*
                import tech.mappie.api.config.UseStrictVisibility

                @UseStrictVisibility
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(5, "Annotation @UseStrictVisibility has no effect on children of EnumMappie")
        }
    }
}