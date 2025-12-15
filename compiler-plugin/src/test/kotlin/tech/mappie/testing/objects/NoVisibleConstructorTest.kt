package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class NoVisibleConstructorTest : MappieTestCase() {

    data class Input(val name: String)

    @ConsistentCopyVisibility
    data class Output private constructor(val name: String)

    @Test
    fun `map object without a visible constructor should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NoVisibleConstructorTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "Target class Output has no visible constructor")
        }
    }
}