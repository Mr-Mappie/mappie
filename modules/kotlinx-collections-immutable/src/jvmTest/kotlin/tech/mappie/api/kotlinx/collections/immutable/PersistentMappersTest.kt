package tech.mappie.api.kotlinx.collections.immutable

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class PersistentMappersTest : MappieTestCase() {

    data class ListWrapper(val value: List<InnerInputWrapper>)
    data class InnerInputWrapper(val value: Int)

    data class PersistentListWrapper(val value: PersistentList<InnerOutputWrapper>)
    data class PersistentSetWrapper(val value: PersistentSet<InnerOutputWrapper>)
    data class InnerOutputWrapper(val value: Int)

    data class PersistentListInput(val value: PersistentList<Int>)
    data class PersistentListOutput(val value: PersistentList<Int>)

    data class PersistentSetInput(val value: PersistentSet<Int>)
    data class PersistentSetOutput(val value: PersistentSet<Int>)

    @Test
    fun `map List to PersistentList implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.PersistentMappersTest.*

                class Mapper : ObjectMappie<ListWrapper, PersistentListWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListWrapper, PersistentListWrapper>()

            assertThat(mapper.map(ListWrapper(listOf(InnerInputWrapper(1), InnerInputWrapper(2)))))
                .isEqualTo(PersistentListWrapper(persistentListOf(InnerOutputWrapper(1), InnerOutputWrapper(2))))
        }
    }

    @Test
    fun `map List to PersistentSet implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.PersistentMappersTest.*

                class Mapper : ObjectMappie<ListWrapper, PersistentSetWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListWrapper, PersistentSetWrapper>()

            assertThat(
                mapper.map(
                    ListWrapper(
                        listOf(
                            InnerInputWrapper(1),
                            InnerInputWrapper(2),
                            InnerInputWrapper(2)
                        )
                    )
                )
            )
                .isEqualTo(PersistentSetWrapper(persistentSetOf(InnerOutputWrapper(1), InnerOutputWrapper(2))))
        }
    }

    @Test
    fun `map PersistentList to PersistentList explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.PersistentMappersTest.*

                class Mapper : ObjectMappie<PersistentListInput, PersistentListOutput>() {
                    override fun map(from: PersistentListInput) = mapping {
                        to::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<PersistentListInput, PersistentListOutput>()

            assertThat(mapper.map(PersistentListInput(persistentListOf(1, 2))))
                .isEqualTo(PersistentListOutput(persistentListOf(1, 2)))
        }
    }

    @Test
    fun `map PersistentSet to PersistentSet explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.PersistentMappersTest.*

                class Mapper : ObjectMappie<PersistentSetInput, PersistentSetOutput>() {
                    override fun map(from: PersistentSetInput) = mapping {
                        to::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<PersistentSetInput, PersistentSetOutput>()

            assertThat(mapper.map(PersistentSetInput(persistentSetOf(1, 2))))
                .isEqualTo(PersistentSetOutput(persistentSetOf(1, 2)))
        }
    }

    @Test
    fun `map PersistentList to PersistentSet implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.PersistentMappersTest.*

                class Mapper : ObjectMappie<PersistentListInput, PersistentSetOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<PersistentListInput, PersistentSetOutput>()

            assertThat(mapper.map(PersistentListInput(persistentListOf(1, 2, 2))))
                .isEqualTo(PersistentSetOutput(persistentSetOf(1, 2)))
        }
    }
}
