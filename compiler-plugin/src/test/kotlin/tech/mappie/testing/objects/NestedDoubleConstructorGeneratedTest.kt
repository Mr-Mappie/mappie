package tech.mappie.testing.objects

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import java.io.File

class NestedDoubleConstructorGeneratedTest {

    data class Input(val a: String, val inner: InnerInput)

    data class InnerInput(val first: String, val second: String) {
        constructor(both: String) : this(both, both)
    }

    data class Output(val b: String, val inner: InnerOutput)

    data class InnerOutput(val first: String, val second: String) {
        constructor(both: String) : this(both, both)
    }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with nested having multiple constructors should succeed`() {
        compile(directory) {
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