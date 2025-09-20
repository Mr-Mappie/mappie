package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.LocalDateTime

class NullableListPropertyToNullableListPropertyTest : MappieTestCase() {
    data class Input(val text: List<LocalDateTime>?)

    data class Output(val text: List<LocalDate>?)

    @Test
    fun `map nested nullable list to nullable list explicit with transform should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.NullableListPropertyToNullableListPropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(LocalDateTime.MIN, LocalDateTime.MAX))))
                .isEqualTo(Output(listOf(LocalDate.MIN, LocalDate.MAX)))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }
}