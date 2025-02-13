package tech.mappie.testing.objects2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappie2Class
import java.io.File

class Object2WithSameValuesTest {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Output(val value: String, val age: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical data classes should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects2.Object2WithSameValuesTest.*

                class Mapper : ObjectMappie2<Input1, Input2, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappie2Class<Input1, Input2, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input1("value"), Input2(10))).isEqualTo(Output("value", 10))
        }
    }
}