package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class NoVisibleConstructorTest {

    data class Input(val name: String)
    data class Output private constructor(val name: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object without a visible constructor should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NoVisibleConstructorTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Target class has no accessible constructor")
        }
    }
}