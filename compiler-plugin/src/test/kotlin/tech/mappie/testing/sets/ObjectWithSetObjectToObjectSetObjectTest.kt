package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithSetObjectToObjectSetObjectTest {
    data class Input(val text: Set<InnerInput>, val int: Int)
    data class InnerInput(val value: String)

    data class Output(val text: Set<InnerOutput>, val int: Int)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map nested set implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.ObjectWithSetObjectToObjectSetObjectTest.*

                class Mapper : ObjectMappie<Input, Output>()

                object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
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

            assertThat(mapper.map(Input(setOf(InnerInput("first"), InnerInput("second")), 20)))
                .isEqualTo(Output(setOf(InnerOutput("first"), InnerOutput("second")), 20))
        }
    }
}