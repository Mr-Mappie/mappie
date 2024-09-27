package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.google.common.base.Objects
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ConstructorParameterNotAFieldTest {

    data class Input(val input: String, val age: Int)
    class Output(output: String, val age: Int) {
        val value = output

        override fun equals(other: Any?) = other is Output && value == other.value && age == other.age
        override fun hashCode(): Int = Objects.hashCode(value, age)
    }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map two classes with unknown parameter set should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorParameterNotAFieldTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        parameter("fake") fromProperty Input::input
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Identifier fake does not occur as as setter or as a parameter in constructor")
        }
    }

    @Test
    fun `map two classes with not compile-time parameter set should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorParameterNotAFieldTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        parameter(0.toString()) fromProperty Input::input
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Identifier must be a compile-time constant.")
        }
    }

    @Test
    fun `map two classes with parameter set should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorParameterNotAFieldTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to("output") fromProperty Input::input
                    }
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

            assertThat(mapper.map(Input("Sjon", 58))).isEqualTo(Output("Sjon", 58))
        }
    }
}