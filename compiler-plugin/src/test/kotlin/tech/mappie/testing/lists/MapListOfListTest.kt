package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MapListOfListTest : MappieTestCase() {

    data class Input(val value: List<List<Int>>)

    data class Output(val value: List<List<Int>>)

    @Test
    fun `map list of lists implicit succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListOfListTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(listOf(1), listOf(2)))))
                .isEqualTo(Output(listOf(listOf(1), listOf(2))))
        }
    }
}