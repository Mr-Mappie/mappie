package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.MappieTestCase
import tech.mappie.testing.compilation.compile
import java.io.File

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
            hasErrorMessage(
                4,
                "No implicit mapping can be generated from InnerInputA to InnerOutputA",
                listOf(
                    "Target InnerOutputA::value automatically resolved from InnerInputA::value but cannot assign source type String to target type Int"
                )
            )
            hasErrorMessage(
                4,
                "No implicit mapping can be generated from InnerInputB to InnerOutputB",
                listOf(
                    "Target InnerOutputB::value automatically resolved from InnerInputB::value but cannot assign source type String to target type Int"
                )
            )
        }
    }
}