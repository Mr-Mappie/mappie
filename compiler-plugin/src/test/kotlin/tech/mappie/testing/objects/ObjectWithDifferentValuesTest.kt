package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithDifferentValuesTest {

    data class Input(val firstname: String, val age: Int)
    data class Output(val name: String, val age: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map two data classes with the different values should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Target Output::name has no source defined")
        }
    }

    @Test
    fun `map two data classes with the different values from KProperty0 should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::name fromProperty from::firstname
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

            assertThat(mapper.map(Input("Stefan", 30))).isEqualTo(Output("Stefan", 30))
        }
    }

    @Test
    fun `map two data classes using to with the different values from KProperty0 should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::name fromProperty from::firstname
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

            assertThat(mapper.map(Input("Stefan", 30))).isEqualTo(Output("Stefan", 30))
        }
    }

    @Test
    fun `map two data classes with the different values from KProperty1 should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.ObjectWithDifferentValuesTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::name fromProperty Input::firstname
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

            assertThat(mapper.map(Input("Sjon", 58))).isEqualTo(Output("Sjon", 58))
        }
    }
}