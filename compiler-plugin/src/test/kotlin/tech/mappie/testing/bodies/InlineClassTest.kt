package tech.mappie.testing.bodies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase
import tech.mappie.testing.compilation.compile

class InlineClassTest : MappieTestCase() {

    data class Input(val name: String)
    data class Output(val name: String)

    @Test
    fun `mapping via nested object function should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.InlineClassTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        val nested = object {
                            var property 
                                get() = ""
                                set(value) = run { println(value) }
                        
                            fun test() = mapping()
                        }
                        return nested.test()
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
    fun `mapping via nested object property should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.InlineClassTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        val lazy by lazy { 1 }
                        val nested = object {
                            var property = mapping()
                        }
                        return nested.property
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
    fun `mapping via nested object with annotation object should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.InlineClassTest.*

                @Suppress("DEPRECATION")
                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        val nested = object {
                            @Deprecated("test")
                            fun test() = mapping {
                                to::name fromValue "value"
                            }
                        }
                        return nested.test()
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("value"))
        }
    }

    @Test
    fun `multiple mappings should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.InlineClassTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        val nested = object {
                            @Deprecated("test")
                            fun test() = mapping()
                        }
                        return mapping()
                    }
                }
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(8,
                "Multiple calls of the function 'mapping' while only one is allowed"
            )
        }
    }
}