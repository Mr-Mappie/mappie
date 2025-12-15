package tech.mappie.api.builtin.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ListMappersTest : MappieTestCase() {

    data class ListInput(val value: List<Int>)
    data class ListOutput(val value: List<Int>)

    @Test
    fun `map List to List explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.collections.IterableToSetMapper
                import tech.mappie.api.builtin.collections.ListMappersTest.*

                class Mapper : ObjectMappie<ListInput, ListOutput>() {
                    override fun map(from: ListInput) = mapping {
                        to::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListInput, ListOutput>()

            assertThat(mapper.map(ListInput(listOf(1, 2))))
                .isEqualTo(ListOutput(listOf(1, 2)))
        }
    }

    @Test
    fun `map List to List implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.collections.ListMappersTest.*

                class Mapper : ObjectMappie<ListInput, ListOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListInput, ListOutput>()

            assertThat(mapper.map(ListInput(listOf(1, 2))))
                .isEqualTo(ListOutput(listOf(1, 2)))
        }
    }
}