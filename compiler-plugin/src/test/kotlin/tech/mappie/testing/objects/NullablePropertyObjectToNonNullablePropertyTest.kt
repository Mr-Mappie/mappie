package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.math.BigDecimal

class NullablePropertyObjectToNonNullablePropertyTest : MappieTestCase() {

    data class Input(val value: BigDecimal?)
    data class Output(val value: BigDecimal)

    @Test
    fun `map object with null property to non-null property should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NullablePropertyObjectToNonNullablePropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasSingleErrorMessage(4, "Target 'Output::value' of type 'BigDecimal' cannot be assigned from 'Input::value' of type 'BigDecimal?'.")
        }
    }
}