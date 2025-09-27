package tech.mappie.testing.bodies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import tech.mappie.testing.compilation.compile

class ExpressionsTest : MappieTestCase() {

    data class Input(val name: String)
    data class Output(val name: String)

    @Test
    fun `mapping using operator should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ExpressionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output =
                        mapping { to::name fromProperty from::name }
                            .let { it.copy(name = it.name + "+") as Output }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name+"))
        }
    }

    @Test
    fun `mapping with lambda should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ExpressionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output =
                        { x: Int -> mapping { to::name fromProperty from::name transform { it + x.toString() } } }(1)
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name1"))
        }
    }

    @Test
    fun `mapping with range test should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ExpressionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output =
                        mapping { to::name fromProperty from::name transform { it + (1 in listOf(0, 2)).toString() } }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("namefalse"))
        }
    }

    @Test
    fun `mapping with function reference should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ExpressionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output =
                        mapping { to::name fromProperty from::name transform(String::toString) }
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
    fun `multiple mappings should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.ExpressionsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        var x = mapping { to::name fromProperty from::name transform(String::toString) }
                        val y = mapping()
                        return x
                    }   
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(6,
                "Multiple calls of the function 'mapping' while only one is allowed"
            )
        }
    }
}