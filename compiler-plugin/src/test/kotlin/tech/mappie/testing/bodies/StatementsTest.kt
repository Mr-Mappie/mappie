package tech.mappie.testing.bodies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tech.mappie.testing.MappieTestCase

class StatementsTest : MappieTestCase() {

    data class Input(val name: String)
    data class Output(val name: String)

    @Test
    fun `mapping with assignment should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.StatementsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    var y = 0
                    override fun map(from: Input): Output {
                        var x = 1
                        y = x + 1
                        return mapping { to::name fromValue y.toString()!! }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("2"))
        }
    }

    @Test
    fun `mapping via if else should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.StatementsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        return if (from.name == "true") {
                            Output("constant")
                        } else {
                            mapping()
                        }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = objectMappie<Input, Output>()

            assertThat(mapper.map(Input("true")))
                .isEqualTo(Output("constant"))

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping via when should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.StatementsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        return when (from) {
                            is Input -> mapping()
                            else -> run { Output("constant") }
                        }
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping via try catch should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.StatementsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        return try { mapping() } catch (e: Exception) { throw e } finally { } 
                    }
                }
                """
            )
        } satisfies {
            isOk()

            val mapper = objectMappie<Input, Output>()
            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping while should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.StatementsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        while (null != ("" ?: null)) {
                            return mapping()
                            break
                            continue
                        }
                        return Output("constant")       
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
    fun `mapping for should succeed`() {
        compile {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.bodies.StatementsTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input): Output {
                        for (i in 0..2) {
                            return mapping();
                        }
                        return Output("constant")
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
}