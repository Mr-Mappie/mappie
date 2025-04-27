package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class MutableListPropertyObjectToListPropertyObjectTest {
    data class Input(val text: MutableList<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: List<InnerOutput>)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map mutable list implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.MutableListPropertyObjectToListPropertyObjectTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(mutableListOf(InnerInput("first"), InnerInput("second")))))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second"))))
        }
    }
}