package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.google.common.base.Objects
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ConstructorParameterNotAFieldTest : MappieTestCase() {

    data class Input(val input: String, val age: Int)
    class Output(output: String, val age: Int) {
        val value = output

        override fun equals(other: Any?) = other is Output && value == other.value && age == other.age
        override fun hashCode(): Int = Objects.hashCode(value, age)
    }

    @Test
    fun `map two classes with unknown parameter set should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorParameterNotAFieldTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to("fake") fromProperty Input::input
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Identifier 'fake' does not occur as a setter or as a parameter in constructor")
        }
    }

    @Test
    fun `map two classes with not compile-time parameter set should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorParameterNotAFieldTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to(0.toString()) fromProperty Input::input
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Argument must be a compile-time constant")
        }
    }

    @Test
    fun `map two classes with parameter set should succeed`() {
        compile {
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
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("Sjon", 58)))
                .isEqualTo(Output("Sjon", 58))
        }
    }
}