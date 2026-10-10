package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class TwoFieldsDifferentTypeTest : MappieTestCase() {

    data class Input(val first: String, val second: String)
    data class Output(val first: Int, val second: Int)

    @Test
    fun `map property implicit should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.TwoFieldsDifferentTypeTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasSingleErrorMessage(4,
                "Multiple targets cannot be assigned:",
                "'Output::first' of type 'Int' cannot be assigned from 'Input::first' of type 'String'.",
                "'Output::second' of type 'Int' cannot be assigned from 'Input::second' of type 'String'.")
        }
    }

    @Test
    fun `map property explicit should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.TwoFieldsDifferentTypeTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::first fromProperty from::first
                        to::second fromProperty from::second
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessages(
                6 to "Target 'Output::first' of type 'Int' cannot be assigned from 'from::first' of type 'String'.",
                7 to "Target 'Output::second' of type 'Int' cannot be assigned from 'from::second' of type 'String'.",
            )
        }
    }
}