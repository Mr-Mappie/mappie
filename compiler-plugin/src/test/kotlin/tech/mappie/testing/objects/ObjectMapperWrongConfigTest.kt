package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ObjectMapperWrongConfigTest : MappieTestCase() {

    data class Input(val value: String)
    data class Output(val value: String)

    @Test
    fun `declaring an ObjectMappie with @UseStrictEnums should warn`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectMapperWrongConfigTest.*
                import tech.mappie.api.config.UseStrictEnums

                @UseStrictEnums
                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(5, "Annotation @UseStrictEnums has no effect on subclass of ObjectMappie")
        }
    }

    @Test
    fun `declaring an ObjectMappie with @UseStrictEnums should not warn when suppressed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectMapperWrongConfigTest.*
                import tech.mappie.api.config.UseStrictEnums

                @Suppress("ANNOTATION_NOT_APPLICABLE")
                @UseStrictEnums
                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()
        }
    }
}