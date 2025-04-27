package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithSetPrimitiveToObjectSetPrimitiveTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: Set<String>)

    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: Set<String>)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested set with generated mapper should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.sets.ObjectWithSetPrimitiveToObjectSetPrimitiveTest.*

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

            assertThat(mapper.map(Input(InnerInput(setOf("first", "second")))))
                .isEqualTo(Output(InnerOutput(setOf("first", "second"))))
        }
    }
}