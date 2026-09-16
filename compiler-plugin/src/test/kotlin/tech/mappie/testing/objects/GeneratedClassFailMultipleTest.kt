package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GeneratedClassFailMultipleTest : MappieTestCase() {
    data class Input(val a: InnerInputA, val b: InnerInputB)
    data class InnerInputA(val value: String)
    data class InnerInputB(val value: String)
    data class Output(val a: InnerOutputA, val b: InnerOutputB)
    data class InnerOutputA(val value: Int)
    data class InnerOutputB(val value: Int)

    @Test
    fun `map object with multiple nested classes without declaring mapping should fail all`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GeneratedClassFailMultipleTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessages(
        4 to "No implicit mapping can be generated from 'InnerInputA' to 'InnerOutputA'.",
// TODO: the following line should be included.
//                4 to "No implicit mapping can be generated from 'InnerInputB' to 'InnerOutputB'",
            )
        }
    }
}