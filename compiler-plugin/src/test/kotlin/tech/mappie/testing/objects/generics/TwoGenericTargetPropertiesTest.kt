package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class TwoGenericTargetPropertiesTest {

    data class Input(
        val a: String,
        val b: Int,
    )

    data class Output<A, B>(
        val a: A,
        val b: B,
    )

    @TempDir
    lateinit var directory: File

    @Test
    fun `map two generic target properties implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.TwoGenericTargetPropertiesTest.*

                class Mapper : ObjectMappie<Input, Output<String, Int>>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output<String, Int>>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("a", 1)))
                .isEqualTo(Output("a", 1))
        }
    }

    @Test
    fun `map two generic target properties explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.TwoGenericTargetPropertiesTest.*

                class Mapper : ObjectMappie<Input, Output<String, Int>>() {
                    override fun map(from: Input) = mapping {
                        to::a fromValue "constant"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output<String, Int>>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("a", 1)))
                .isEqualTo(Output("constant", 1))
        }
    }
}