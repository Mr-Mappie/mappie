package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class DoubleNestedObjectWithEnumToDoubleNestedObjectWithEnumWithSameEntriesTest {

    data class Input(val nested: NestedInput)
    data class NestedInput(val nested: NestedNestedEnumInput)
    @Suppress("unused") enum class NestedNestedEnumInput { A, B, C }

    data class Output(val nested: NestedOutput)
    data class NestedOutput(val nested: NestedNestedEnumOutput)
    @Suppress("unused") enum class NestedNestedEnumOutput { A, B, C }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with both mappers generated should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.DoubleNestedObjectWithEnumToDoubleNestedObjectWithEnumWithSameEntriesTest.*

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

            assertThat(mapper.map(Input(NestedInput(NestedNestedEnumInput.A))))
                .isEqualTo(Output(NestedOutput(NestedNestedEnumOutput.A)))
        }
    }

    @Test
    fun `map object with explicit inner mapper and generated inner-most mapper should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.DoubleNestedObjectWithEnumToDoubleNestedObjectWithEnumWithSameEntriesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    object NestedMapper : ObjectMappie<NestedInput, NestedOutput>()
                }
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

            assertThat(mapper.map(Input(NestedInput(NestedNestedEnumInput.A))))
                .isEqualTo(Output(NestedOutput(NestedNestedEnumOutput.A)))
        }
    }
}