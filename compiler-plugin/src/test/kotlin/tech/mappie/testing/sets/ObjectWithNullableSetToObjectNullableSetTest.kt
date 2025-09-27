package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.LocalDateTime

class ObjectWithNullableSetToObjectNullableSetTest : MappieTestCase() {
    data class Input(val text: Set<LocalDateTime>?)

    data class Output(val text: Set<LocalDate>?)

    @Test
    fun `map nested nullable set to nullable set explicit with transform should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.ObjectWithNullableSetToObjectNullableSetTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(setOf(LocalDateTime.MIN, LocalDateTime.MAX))))
                .isEqualTo(Output(setOf(LocalDate.MIN, LocalDate.MAX)))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }
}