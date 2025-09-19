package tech.mappie.testing.bodies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class StatementsTest {

    data class Input(val name: String)
    data class Output(val name: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapping with assignment should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("2"))
        }
    }

    @Test
    fun `mapping via if else should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("true")))
                .isEqualTo(Output("constant"))

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping via when should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping via try catch should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping while should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping for should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name"))
        }
    }
}