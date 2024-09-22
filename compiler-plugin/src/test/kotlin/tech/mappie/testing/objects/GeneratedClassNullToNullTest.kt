package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GeneratedClassNullToNullTest {
    data class Input(val a: InnerInput?)
    enum class InnerInput { FIRST, SECOND }
    data class Output(val a: InnerOutput?)
    enum class InnerOutput { FIRST, SECOND }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested nullable to nullable without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.GeneratedClassNullToNullTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()


            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(InnerInput.FIRST)))
                .isEqualTo(Output(InnerOutput.FIRST))

            assertThat(mapper.map(Input(null)))
                .isEqualTo(Output(null))
        }
    }
}