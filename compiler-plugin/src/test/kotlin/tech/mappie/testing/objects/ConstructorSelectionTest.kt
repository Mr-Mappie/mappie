package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ConstructorSelectionTest : MappieTestCase() {

    data class Input(val name: String)
    data class Output(val name: String, val age: Int) {
        constructor(name: String) : this(name, -1)
    }

    @Test
    fun `map object with all values should call primary constructor`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::age fromValue 50
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("value")))
                .isEqualTo(Output("value", 50))
        }
    }

    @Test
    fun `map object with only values of secondary constructor should call secondary constructor`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("value")))
                .isEqualTo(Output("value", -1))
        }
    }
}