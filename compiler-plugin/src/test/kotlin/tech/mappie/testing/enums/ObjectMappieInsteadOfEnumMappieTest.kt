package tech.mappie.testing.enums

import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File
import kotlin.test.Test

class ObjectMappieInsteadOfEnumMappieTest {

    enum class Input { TRUE, FALSE }

    enum class Output { TRUE, FALSE }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map enum to expression should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.ObjectMappieInsteadOfEnumMappieTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies  {
            isCompilationError()
            hasErrorMessage(4, "Source type Input cannot be an enum class")
            hasErrorMessage(4, "Target type Output cannot be an enum class")
        }
    }
}
