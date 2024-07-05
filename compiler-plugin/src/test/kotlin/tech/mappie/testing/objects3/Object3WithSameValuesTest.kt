package tech.mappie.testing.objects3

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappie3Class
import java.io.File

class Object3WithSameValuesTest {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Input3(val char: Char)
    data class Output(val value: String, val age: Int, val char: Char)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map identical data classes should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie3
                        import tech.mappie.testing.objects3.Object3WithSameValuesTest.*
    
                        class Mapper : ObjectMappie3<Input1, Input2, Input3, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

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