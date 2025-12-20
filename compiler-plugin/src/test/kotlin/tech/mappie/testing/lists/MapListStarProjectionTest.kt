package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MapListStarProjectionTest : MappieTestCase() {

    data class StringList(val value: List<String>)

    data class StarList(val value: List<*>)

    @Test
    fun `map list to list of star projection should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListStarProjectionTest.*

                class Mapper : ObjectMappie<StringList, StarList>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<StringList, StarList>()

            assertThat(mapper.map(StringList(listOf("a", "b"))))
                .isEqualTo(StarList(listOf("a", "b")))
        }
    }

    @Test
    fun `map list of star projection to list of string should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListStarProjectionTest.*

                class Mapper : ObjectMappie<StarList, StringList>()
                """
            )
        } satisfies {
            isCompilationError()
            hasNoWarningsOrErrors() // TODO

            val mapper = objectMappie<StarList, StringList>()

            assertThat(mapper.map(StarList(listOf("a", "b"))))
                .isEqualTo(StringList(listOf("a", "b")))
        }
    }
}