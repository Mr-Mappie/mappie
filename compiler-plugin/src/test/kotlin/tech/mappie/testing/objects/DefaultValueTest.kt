package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import tech.mappie.testing.loadObjectMappieClass

class DefaultValueTest : MappieTestCase() {

    data class Input(val name: String)
    data class Output(val name: String, val age: Int = 10)

    @Test
    fun `map two data classes with a default argument not set should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.DefaultValueTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name", 10))
        }
    }

    @Test
    fun `map two data classes with a default argument not set and option enabled should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.DefaultValueTest.*
                import tech.mappie.api.config.UseDefaultArguments

                @UseDefaultArguments
                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name", 10))
        }
    }

    @Test
    fun `map two data classes with a default argument not set and option disabled should fail`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.DefaultValueTest.*
                import tech.mappie.api.config.UseDefaultArguments

                @UseDefaultArguments(false)
                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(5, "Target Output::age has no source defined")
        }
    }

    @Test
    fun `map two data classes with a default argument set should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.DefaultValueTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::age fromValue 20
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name", 20))
        }
    }
}