package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class NullablePropertyToNonNullablePropertyTest {
    data class Input(val value: String?)
    data class Output(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map object with null property to non-null property should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NullablePropertyToNonNullablePropertyTest.*

                class Mapper : ObjectMappie<Input, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage(4, "Target Output::value automatically resolved from Input::value but cannot assign source type String? to target type String")
        }
    }

    @Test
    fun `map object null property to non-null property fromProperty and transform should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NullablePropertyToNonNullablePropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::value fromProperty Input::value transform { it ?: "null" }
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

            assertThat(mapper.map(Input(null))).isEqualTo(Output("null"))
            assertThat(mapper.map(Input("value"))).isEqualTo(Output("value"))
        }
    }

    @Test
    fun `map object null property to non-null property fromPropertyNotNull and transform should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.NullablePropertyToNonNullablePropertyTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        to::value fromPropertyNotNull from::value transform { it + " test" }
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

            assertThat(mapper.map(Input("value"))).isEqualTo(Output("value test"))
        }
    }
}