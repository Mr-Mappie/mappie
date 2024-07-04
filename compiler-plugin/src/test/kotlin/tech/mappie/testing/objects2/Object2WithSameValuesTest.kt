package tech.mappie.testing.objects2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappie2Class
import java.io.File

class Object2WithSameValuesTest {

    data class Input1(val value: String)
    data class Input2(val age: Int)
    data class Output(val value: String, val age: Int)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map identical data classes should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie2
                        import tech.mappie.testing.objects2.Object2WithSameValuesTest.*
    
                        class Mapper : ObjectMappie2<Input1, Input2, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadObjectMappie2Class<Input1, Input2, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input1("value"), Input2(10))).isEqualTo(Output("value", 10))
        }
    }
}