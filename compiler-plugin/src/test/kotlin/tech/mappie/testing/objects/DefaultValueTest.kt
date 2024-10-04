package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class DefaultValueTest {

    data class Input(val name: String)
    data class Output(val name: String, val age: Int = 10)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map two data classes with a default argument not set should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.DefaultValueTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name"))).isEqualTo(Output("name", 10))
        }
    }

    @Test
    fun `map two data classes with a default argument set should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.DefaultValueTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::age fromValue 20
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("name"))).isEqualTo(Output("name", 20))
        }
    }
}