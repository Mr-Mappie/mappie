package tech.mappie.testing.bodies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ScopeFunctionsTest {

    data class Input(val name: String)
    data class Output(val name: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapping assigned to variable with apply should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name"))).isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping with also should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name"))).isEqualTo(Output("name"))
        }
    }

    @Test
    fun `mapping with let should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name"))).isEqualTo(Output("constant"))
        }
    }

    @Test
    fun `mapping with run should succeed`() {
        compile(directory) {
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

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name"))).isEqualTo(Output("name"))
        }
    }
}