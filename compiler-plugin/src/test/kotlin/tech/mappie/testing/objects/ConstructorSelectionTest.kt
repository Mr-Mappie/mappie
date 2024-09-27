package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ConstructorSelectionTest {

    data class Input(val name: String)
    data class Output(val name: String, val age: Int) {
        constructor(name: String) : this(name, -1)
    }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with all values should call primary constructor`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorSelectionTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::age fromValue 50
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

            assertThat(mapper.map(Input("value")))
                .isEqualTo(Output("value", 50))
        }
    }

    @Test
    fun `map object with only values of secondary constructor should call secondary constructor`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ConstructorSelectionTest.*

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

            assertThat(mapper.map(Input("value")))
                .isEqualTo(Output("value", -1))
        }
    }
}