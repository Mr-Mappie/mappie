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
}