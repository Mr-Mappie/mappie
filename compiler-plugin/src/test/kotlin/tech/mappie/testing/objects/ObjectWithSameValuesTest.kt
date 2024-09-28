package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithSameValuesTest {

    data class Input(val value: String)
    data class Output(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical objects should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithSameValuesTest.*

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

            assertThat(mapper.map(Input("value"))).isEqualTo(Output("value"))
        }
    }

    @Test
    @Disabled("Not implemented yet")
    fun `map identical objects with an explicit mapping should warn`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithSameValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::value fromProperty Input::value
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasWarningMessage(6, "Unnecessary explicit mapping of target Output::value")

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("value"))).isEqualTo(Output("value"))
        }
    }
}