package tech.mappie.testing.objects3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile

import tech.mappie.testing.loadObjectMappie3Class
import java.io.File

class Object3WithSameValuesTest {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Input3(val char: Char)
    data class Output(val value: String, val age: Int, val char: Char)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical data classes should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie3
                import tech.mappie.testing.objects3.Object3WithSameValuesTest.*

                class Mapper : ObjectMappie3<Input1, Input2, Input3, Output>()
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappie3Class<Input1, Input2, Input3, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input1("value"), Input2(10), Input3('c')))
                .isEqualTo(Output("value", 10, 'c'))
        }
    }
}