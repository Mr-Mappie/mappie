package tech.mappie.api.builtin.collections

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class SetMappersTest : MappieTestCase() {

    data class ListInput(val value: List<Int>)
    data class SetInput(val value: Set<Int>)

    data class SetOutput(val value: Set<Int>)

    @Test
    fun `map List to Set explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.collections.IterableToSetMapper
                import tech.mappie.api.builtin.collections.SetMappersTest.*

                class Mapper : ObjectMappie<ListInput, SetOutput>() {
                    override fun map(from: ListInput) = mapping {
                        to::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListInput, SetOutput>()

            assertThat(mapper.map(ListInput(listOf(1, 2))))
                .isEqualTo(SetOutput(setOf(1, 2)))
        }
    }

    @Test
    fun `map Set to Set implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.builtin.collections.SetMappersTest.*

                class Mapper : ObjectMappie<SetInput, SetOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<SetInput, SetOutput>()

            assertThat(mapper.map(SetInput(setOf(1, 2))))
                .isEqualTo(SetOutput(setOf(1, 2)))
        }
    }
}