package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File
import java.math.BigDecimal

class NullablePropertyObjectToNonNullablePropertyTest {
    data class Input(val value: BigDecimal?)
    data class Output(val value: BigDecimal)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with null property to non-null property should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NullablePropertyObjectToNonNullablePropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            // TODO: error message also contains `No implicit mapping can be generated from BigDecimal? to BigDecimal` which should not happen.
            hasErrorMessage(4, "Target Output::value automatically resolved from Input::value but cannot assign source type BigDecimal? to target type BigDecimal")
        }
    }
}