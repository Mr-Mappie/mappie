package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.LocalDateTime

class MapListTest : MappieTestCase() {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @Test
    fun `mapListNullable implicit succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapListTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.mapList(listOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX))))
                .isEqualTo(listOf(Output(LocalDate.MIN), Output(LocalDate.MAX)))
        }
    }
}