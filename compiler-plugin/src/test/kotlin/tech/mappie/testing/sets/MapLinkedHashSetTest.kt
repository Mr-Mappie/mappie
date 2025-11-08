package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MapLinkedHashSetTest : MappieTestCase() {

    data class Input(val value: LinkedHashSet<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val value: LinkedHashSet<InnerOutput>)
    data class InnerOutput(val value: String)

    @Test
    fun `map LinkedHashSet implicit succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.MapLinkedHashSetTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(LinkedHashSet(listOf(InnerInput("element"))))))
                .isEqualTo(Output(LinkedHashSet(emptyList())))
        }
    }

    @Test
    fun `map LinkedHashSet explicit succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.api.Mappie1
                import tech.mappie.testing.sets.MapLinkedHashSetTest.*

                class Mapper : ObjectMappie<Input, Output>()

                class LinkedHashSetMapper<T, R>(private val inner: Mappie1<T, R>) : ObjectMappie<LinkedHashSet<T>, LinkedHashSet<R>>() {
                    override fun map(from: LinkedHashSet<T>) = LinkedHashSet<R>(from.map(inner::map))
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(LinkedHashSet(listOf(InnerInput("element"))))))
                .isEqualTo(Output(LinkedHashSet(listOf(InnerOutput("element")))))
        }
    }
}