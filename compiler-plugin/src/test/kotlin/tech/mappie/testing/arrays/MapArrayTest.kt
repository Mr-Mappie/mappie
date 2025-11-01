package tech.mappie.testing.arrays

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.api.mapArray
import tech.mappie.testing.MappieTestCase
import java.time.LocalDate
import java.time.LocalDateTime

class MapArrayTest : MappieTestCase() {

    data class Input(val value: LocalDateTime)

    data class Output(val value: LocalDate)

    @Test
    fun `mapArray implicit succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.arrays.MapArrayTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.mapArray(arrayOf(Input(LocalDateTime.MIN), Input(LocalDateTime.MAX))))
                .isEqualTo(arrayOf(Output(LocalDate.MIN), Output(LocalDate.MAX)))
        }
    }

    @Test
    fun `mapArray implicit and empty succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.arrays.MapArrayTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.mapArray(emptyArray()))
                .isInstanceOf(Array<Output>::class.java)
                .isEmpty()
        }
    }
}