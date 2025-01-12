package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class ObjectMapperWrongConfigTest {

    data class Input(val value: String)
    data class Output(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `declaring an ObjectMappie with @UseStrictEnums should warn`() {
        compile(directory) {
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
            hasWarningMessage(5, "Annotation @UseStrictEnums has no effect on children of ObjectMappie")
        }
    }

    @Test
    fun `declaring an ObjectMappie with @UseStrictEnums should not warn when suppressed`() {
        compile(directory) {
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
            hasNoMessages()
        }
    }
}