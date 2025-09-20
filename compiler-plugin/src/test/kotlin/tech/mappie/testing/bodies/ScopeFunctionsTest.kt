package tech.mappie.testing.bodies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class ScopeFunctionsTest : MappieTestCase() {

    data class Input(val name: String)
    data class Output(val name: String)

    @Test
    fun `mapping assigned to variable with apply should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ScopeFunctionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        val result = mapping()
                        return result.apply { println(this) }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping with also should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ScopeFunctionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        val result = mapping().also { println("Test") }
                        return result
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping with let should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ScopeFunctionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output = mapping()
                        .let { Output("constant") }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("constant"))
        }
    }

    @Test
    fun `mapping with run should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ScopeFunctionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output = mapping()
                        .run { this }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }
}