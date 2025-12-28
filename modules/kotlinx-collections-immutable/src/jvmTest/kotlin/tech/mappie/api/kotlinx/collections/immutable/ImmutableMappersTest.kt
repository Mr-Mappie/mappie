package tech.mappie.api.kotlinx.collections.immutable

import kotlinx.collections.immutable.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ImmutableMappersTest : MappieTestCase() {

    data class ListWrapper(val value: List<InnerInputWrapper>)
    data class InnerInputWrapper(val value: Int)

    data class ImmutableListWrapper(val value: ImmutableList<InnerOutputWrapper>)
    data class ImmutableSetWrapper(val value: ImmutableSet<InnerOutputWrapper>)
    data class InnerOutputWrapper(val value: Int)

    data class ImmutableListInput(val value: ImmutableList<Int>)
    data class ImmutableListOutput(val value: ImmutableList<Int>)

    data class ImmutableSetInput(val value: ImmutableSet<Int>)
    data class ImmutableSetOutput(val value: ImmutableSet<Int>)

    @Test
    fun `map List to ImmutableList implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.ImmutableMappersTest.*

                class Mapper : ObjectMappie<ListWrapper, ImmutableListWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListWrapper, ImmutableListWrapper>()

            assertThat(mapper.map(ListWrapper(listOf(InnerInputWrapper(1), InnerInputWrapper(2)))))
                .isEqualTo(ImmutableListWrapper(immutableListOf(InnerOutputWrapper(1), InnerOutputWrapper(2))))
        }
    }

    @Test
    fun `map List to ImmutableSet implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.ImmutableMappersTest.*

                class Mapper : ObjectMappie<ListWrapper, ImmutableSetWrapper>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ListWrapper, ImmutableSetWrapper>()

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
                .isEqualTo(ImmutableSetWrapper(immutableSetOf(InnerOutputWrapper(1), InnerOutputWrapper(2))))
        }
    }

    @Test
    fun `map ImmutableList to ImmutableList explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.ImmutableMappersTest.*

                class Mapper : ObjectMappie<ImmutableListInput, ImmutableListOutput>() {
                    override fun map(from: ImmutableListInput) = mapping {
                        to::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ImmutableListInput, ImmutableListOutput>()

            assertThat(mapper.map(ImmutableListInput(persistentListOf(1, 2))))
                .isEqualTo(ImmutableListOutput(persistentListOf(1, 2)))
        }
    }

    @Test
    fun `map ImmutableSet to ImmutableSet explicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.ImmutableMappersTest.*

                class Mapper : ObjectMappie<ImmutableSetInput, ImmutableSetOutput>() {
                    override fun map(from: ImmutableSetInput) = mapping {
                        to::value fromProperty from::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ImmutableSetInput, ImmutableSetOutput>()

            assertThat(mapper.map(ImmutableSetInput(immutableSetOf(1, 2))))
                .isEqualTo(ImmutableSetOutput(immutableSetOf(1, 2)))
        }
    }

    @Test
    fun `map ImmutableList to ImmutableSet implicit should succeed`() {
        compile {
            file(
                "Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.kotlinx.collections.immutable.ImmutableMappersTest.*

                class Mapper : ObjectMappie<ImmutableListInput, ImmutableSetOutput>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<ImmutableListInput, ImmutableSetOutput>()

            assertThat(mapper.map(ImmutableListInput(immutableListOf(1, 2, 2))))
                .isEqualTo(ImmutableSetOutput(immutableSetOf(1, 2)))
        }
    }
}
