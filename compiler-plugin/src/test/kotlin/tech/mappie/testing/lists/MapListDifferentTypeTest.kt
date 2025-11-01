package tech.mappie.testing.lists

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MapListDifferentTypeTest : MappieTestCase() {

    data class Input(val value: List<String>)

    data class Output(val value: List<Int>)

    @Test
    fun `map list with different type should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListDifferentTypeTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "No implicit mapping can be generated from String to Int")
        }
    }
}