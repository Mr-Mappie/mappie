package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.LocalDateTime

class MapNullableSetTest : MappieTestCase() {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @Test
    fun `mapNullableSet implicit succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.MapNullableSetTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.mapNullableSet(setOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX))))
                .isEqualTo(setOf(Output(LocalDate.MIN), Output(LocalDate.MAX)))

            assertThat(mapper.mapNullableSet(null))
                .isEqualTo(null)
        }
    }
}