package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GenericSourceAndTargetTest {

    data class Input<A, B>(
        val a: A,
        val b: B,
    )

    data class Output<A, B>(
        val a: A,
        val b: B,
    )

    @TempDir
    lateinit var directory: File

    @Test
    fun `map generic source target properties implicit should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceAndTargetTest.*

                class Mapper : ObjectMappie<Input<String, Int>, Output<String, Int>>()
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input<String, Int>, Output<String, Int>>("Mapper")
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
                import tech.mappie.testing.objects.generics.GenericSourceAndTargetTest.*

                class Mapper : ObjectMappie<Input<Int, String>, Output<Int, String>>() {
                    override fun map(from: Input<Int, String>) = mapping {
                        to::a fromProperty from::a
                        to::b fromValue "constant"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input<Int, String>, Output<Int, String>>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(1, "b")))
                .isEqualTo(Output(1, "constant"))
        }
    }

    @Test
    fun `map two generic target properties explicit with transform should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericSourceAndTargetTest.*

                class Mapper : ObjectMappie<Input<Int, Int>, Output<String, String>>() {
                    override fun map(from: Input<Int, Int>) = mapping {
                        to::a fromProperty from::a transform { it.toString() }
                        to::b fromValue "10"
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoWarningsOrErrors()

            val mapper = classLoader
                .loadObjectMappieClass<Input<Int, Int>, Output<String, String>>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(1, 2)))
                .isEqualTo(Output("1", "10"))
        }
    }
}