package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class DoubleNestedObjectWithEnumToDoubleNestedObjectWithEnumWithSameEntriesTest : MappieTestCase() {

    data class Input(val nested: NestedInput)
    data class NestedInput(val nested: NestedNestedEnumInput)
    enum class NestedNestedEnumInput { A, B, C }

    data class Output(val nested: NestedOutput)
    data class NestedOutput(val nested: NestedNestedEnumOutput)
    enum class NestedNestedEnumOutput { A, B, C }

    @Test
    fun `map object with both mappers generated should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.enums.DoubleNestedObjectWithEnumToDoubleNestedObjectWithEnumWithSameEntriesTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(NestedInput(NestedNestedEnumInput.A))))
                .isEqualTo(Output(NestedOutput(NestedNestedEnumOutput.A)))
        }
    }

    @Test
    fun `map object with explicit inner mapper and generated inner-most mapper should succeed`() {
        compile {
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
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input(NestedInput(NestedNestedEnumInput.A))))
                .isEqualTo(Output(NestedOutput(NestedNestedEnumOutput.A)))
        }
    }
}