package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GenericWithUpperBoundTest {
    interface InputInterface { val id: String }
    data class Input(override val id: String) : InputInterface
    data class Output(val id: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map from generic interface upper bound implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GenericWithUpperBoundTest.*

                class Mapper<FROM : InputInterface> : ObjectMappie<FROM, Output>()
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

            assertThat(mapper.map(Input("value"))).isEqualTo(Output("value"))
        }
    }

    @Test
    fun `map from generic interface upper bound explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.GenericWithUpperBoundTest.*

                class Mapper<FROM : InputInterface> : ObjectMappie<FROM, Output>() {
                    override fun map(from: FROM) = mapping {
                        to::id fromProperty from::id
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

            assertThat(mapper.map(Input("value"))).isEqualTo(Output("value"))
        }
    }
}