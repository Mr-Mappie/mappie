package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class NestedDoubleConstructorGeneratedTest : MappieTestCase() {

    data class Input(val a: String, val inner: InnerInput)

    data class InnerInput(val first: String, val second: String) {
        constructor(both: String) : this(both, both)
    }

    data class Output(val b: String, val inner: InnerOutput)

    data class InnerOutput(val first: String, val second: String) {
        constructor(both: String) : this(both, both)
    }

    @Test
    fun `map object with nested having multiple constructors should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NestedDoubleConstructorGeneratedTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::b fromProperty from::a
                    }
                }
                """
            )
        } satisfies {
            isOk()
        }
    }
}