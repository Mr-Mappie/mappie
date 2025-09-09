package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ObjectWithDifferentValuesTest : MappieTestCase() {

    data class Input(val firstname: String, val age: Int)
    data class Output(val name: String, val age: Int)

    @Test
    fun `map two data classes with the different values should fail`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "Target Output::name has no source defined")
        }
    }

    @Test
    fun `map two data classes with the different values from KProperty0 should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::name fromProperty from::firstname
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>("Mapper").map(Input("Stefan", 30)))
                .isEqualTo(Output("Stefan", 30))
        }
    }

    @Test
    fun `map two data classes using to with the different values from KProperty0 should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::name fromProperty from::firstname
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input("Stefan", 30)))
                .isEqualTo(Output("Stefan", 30))
        }
    }

    @Test
    fun `map two data classes with the different values from KProperty1 should succeed`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::name fromProperty Input::firstname
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>("Mapper").map(Input("Sjon", 58)))
                .isEqualTo(Output("Sjon", 58))
        }
    }

    @Test
    fun `map property fromProperty should succeed with method reference transform`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::age fromProperty from::firstname transform String::toInt
                        Output::name fromProperty from::age transform Int::toString
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            assertThat(objectMappie<Input, Output>().map(Input("101", 9)))
                .isEqualTo(Output("9", 101))
        }
    }

    @Test
    fun `map property fromProperty should fail with method reference with wrong signature`() {
        compile {
            file("Mapper.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::age fromProperty from::firstname transform String::toString
                        Output::name fromProperty from::age transform Int::toInt
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6, "Inapplicable candidate(s): fun toString(): String")
            hasErrorMessage(7, "Inapplicable candidate(s): fun toInt(): Int")
        }
    }
}