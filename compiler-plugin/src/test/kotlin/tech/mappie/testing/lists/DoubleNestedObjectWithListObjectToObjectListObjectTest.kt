package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class DoubleNestedObjectWithListObjectToObjectListObjectTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: List<InnerInnerInput>)
    data class InnerInnerInput(val value: String)

    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: List<InnerInnerOutput>)
    data class InnerInnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested list with generated mapping should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.DoubleNestedObjectWithListObjectToObjectListObjectTest.*

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

            assertThat(mapper.map(Input(InnerInput(listOf(InnerInnerInput("first"), InnerInnerInput("second"))))))
                .isEqualTo(Output(InnerOutput(listOf(InnerInnerOutput("first"), InnerInnerOutput("second")))))
        }
    }

    @Test
    fun `map object with nested list mapping should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.lists.DoubleNestedObjectWithListObjectToObjectListObjectTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::a fromProperty from::a
                    }
                }
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

            assertThat(mapper.map(Input(InnerInput(listOf(InnerInnerInput("first"), InnerInnerInput("second"))))))
                .isEqualTo(Output(InnerOutput(listOf(InnerInnerOutput("first"), InnerInnerOutput("second")))))
        }
    }
}