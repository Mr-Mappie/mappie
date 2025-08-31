package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.LocalDateTime

class MapNullableListTest : MappieTestCase() {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @Test
    fun `mapNullableList implicit succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapNullableListTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.mapNullableList(listOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX))))
                .isEqualTo(listOf(Output(LocalDate.MIN), Output(LocalDate.MAX)))

            assertThat(mapper.mapNullableList(null))
                .isEqualTo(null)
        }
    }
}