package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class GeneratedClassFailMultipleTest : MappieTestCase() {
    data class Input(val a: InnerInputA, val b: BranchInput)
    data class BranchInput(val child: InnerInputB)
    data class InnerInputA(val value: String)
    data class InnerInputB(val value: String)
    data class Output(val a: InnerOutputA, val b: BranchOutput)
    data class BranchOutput(val child: InnerOutputB)
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
            hasSingleErrorMessage(
                4, "Implicit mappings cannot be generated:",
                "'InnerInputA' to 'InnerOutputA'",
                "'InnerInputB' to 'InnerOutputB'",
            )
        }
    }

    @Test
    fun `report missing mappings between types with the same simple names`() {
        compile {
            for (pkg in listOf("first", "second")) {
                file("$pkg.kt",
                    """
                    package $pkg
                    data class Source(val value: String)
                    data class Target(val value: Int)
                    """
                )
            }
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie

                data class Input(val a: first.Source, val b: second.Source)
                data class Output(val a: first.Target, val b: second.Target)
                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasSingleErrorMessage(
                5, "Implicit mappings cannot be generated:",
                "'first.Source' to 'first.Target'",
                "'second.Source' to 'second.Target'",
            )
        }
    }
}
