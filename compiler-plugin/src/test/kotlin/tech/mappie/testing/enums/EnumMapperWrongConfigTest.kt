package tech.mappie.testing.enums

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class EnumMapperWrongConfigTest : MappieTestCase() {

    enum class Input { A, B }
    enum class Output { A, B }

    @Test
    fun `declaring an EnumMappie with @UseDefaultArguments should warn`() {
        compile {
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
            hasWarningMessage(5, "Annotation @UseDefaultArguments has no effect on subclass of EnumMappie")
        }
    }

    @Test
    fun `declaring an EnumMappie with @UseDefaultArguments should not warn when suppressed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumMapperWrongConfigTest.*
                import tech.mappie.api.config.UseDefaultArguments

                @Suppress("ANNOTATION_USE_DEFAULT_ARGUMENTS_NOT_APPLICABLE")
                @UseDefaultArguments
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }

    @Test
    fun `declaring an EnumMappie with @UseStrictVisibility should warn`() {
        compile {
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
            hasWarningMessage(5, "Annotation @UseStrictVisibility has no effect on subclass of EnumMappie")
        }
    }

    @Test
    fun `declaring an EnumMappie with @UseStrictVisibility should not warn when suppressed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.EnumMappie
                import tech.mappie.testing.enums.EnumMapperWrongConfigTest.*
                import tech.mappie.api.config.UseStrictVisibility

                @Suppress("ANNOTATION_USE_STRICT_VISIBILITY_NOT_APPLICABLE")
                @UseStrictVisibility
                class Mapper : EnumMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }
}