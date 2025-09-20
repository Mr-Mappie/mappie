package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.MappieTestCase
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class AbstractListPropertyObjectToListPropertyObjectTest : MappieTestCase() {

    data class Input(val text: AbstractList<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: List<InnerOutput>)
    data class InnerOutput(val value: String)

    @Test
    fun `map abstract list implicit should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.AbstractListPropertyObjectToListPropertyObjectTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            val list = object : AbstractList<InnerInput>() {
                override val size = 2

                override fun get(index: Int) = when(index) {
                    0 -> InnerInput("first")
                    1 -> InnerInput("second")
                    else -> throw IndexOutOfBoundsException()
                }
            }
            assertThat(mapper.map(Input(list)))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }
}