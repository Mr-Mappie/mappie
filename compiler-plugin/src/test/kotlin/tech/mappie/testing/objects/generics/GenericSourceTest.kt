package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GenericSourceTest {

    data class Input<A, B>(
        val a: A,
        val b: B,
    )

    data class Output(
        val a: Int,
        val b: String,
    )

    @TempDir
    lateinit var directory: File

    @Test
    fun `map from generic source property implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceTest.*

                class Mapper : ObjectMappie<Input<Int, String>, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input<Int, String>, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(1, "b")))
                .isEqualTo(Output(1, "b"))
        }
    }

    @Test
    fun `map from generic source property explicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceTest.*

                class Mapper : ObjectMappie<Input<Int, String>, Output>() {
                    override fun map(from: Input<Int, String>) = mapping {
                        to::b fromValue "constant"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input<Int, String>, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(1, "b")))
                .isEqualTo(Output(1, "constant"))
        }
    }
}