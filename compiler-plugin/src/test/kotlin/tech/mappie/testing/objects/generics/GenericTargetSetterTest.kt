package tech.mappie.testing.objects.generics

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GenericTargetSetterTest {

    data class Input(
        val a: String,
        val b: Int,
    )

    data class Output<A, B>(val b: B) {
        var a: A? = null
    }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map to generic target setter should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.generics.GenericTargetSetterTest.*

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
                .isEqualTo(Output<String, Int>(1).apply { a = "a" })
        }
    }
}