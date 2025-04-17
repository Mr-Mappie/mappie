package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ListPropertyPrimitiveToListPropertyPrimitiveTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: List<String>)

    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: List<String>)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested list implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.ListPropertyPrimitiveToListPropertyPrimitiveTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerInput(listOf("first", "second")))))
                .isEqualTo(Output(InnerOutput(listOf("first", "second"))))
        }
    }
}