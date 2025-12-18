package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MapListStarProjectionTest : MappieTestCase() {

    data class Input(val value: List<String>)

    data class Output(val value: List<*>)

    @Test
    fun `map list to list of star projection should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListStarProjectionTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf("a", "b"))))
                .isEqualTo(Output(listOf("a", "b")))
        }
    }

    @Test
    fun `map list of star projection to list of string should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListStarProjectionTest.*

                class Mapper : ObjectMappie<Output, Input>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Output, Input>()

            assertThat(mapper.map(Output(listOf("a", "b"))))
                .isEqualTo(Input(listOf("a", "b")))
        }
    }
}