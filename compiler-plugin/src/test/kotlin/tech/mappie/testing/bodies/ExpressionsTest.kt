package tech.mappie.testing.bodies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ExpressionsTest {

    data class Input(val name: String)
    data class Output(val name: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapping using operator should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name")))
                .isEqualTo(Output("name+"))
        }
    }

    @Test
    fun `mapping with lambda should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

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