package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class MapTwoListsOfDifferentTypeTest : MappieTestCase() {

    data class Input(val first: List<FirstInput>, val second: List<SecondInput>)
    data class FirstInput(val value: String)
    data class SecondInput(val value: Int)

    data class Output(val first: List<FirstOutput>, val second: List<SecondOutput>)
    data class FirstOutput(val value: String)
    data class SecondOutput(val value: Int)

    @Test
    fun `map two lists of different type should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MapTwoListsOfDifferentTypeTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(listOf(FirstInput("a")), listOf(SecondInput(1)))))
                .isEqualTo(Output(listOf(FirstOutput("a")), listOf(SecondOutput(1))))
        }
    }
}