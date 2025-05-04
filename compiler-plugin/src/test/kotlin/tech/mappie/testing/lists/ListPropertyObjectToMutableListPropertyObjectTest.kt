package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ListPropertyObjectToMutableListPropertyObjectTest {
    data class Input(val text: List<InnerInput>)
    data class InnerInput(val value: String)

    data class Output(val text: MutableList<InnerOutput>)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map mutable list implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyObjectToMutableListPropertyObjectTest.*

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

            val result = mapper.map(Input(listOf(InnerInput("first"), InnerInput("second"))))
            assertThat(result)
                .isEqualTo(Output(mutableListOf(InnerOutput("first"), InnerOutput("second"))))

            assertDoesNotThrow { result.text.add(InnerOutput("third")) }
        }
    }
}