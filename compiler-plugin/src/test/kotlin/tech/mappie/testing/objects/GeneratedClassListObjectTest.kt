package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GeneratedClassListObjectTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: List<InnerInnerInput>)
    data class InnerInnerInput(val value: String)
    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: List<InnerInnerOutput>)
    data class InnerInnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested list without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.GeneratedClassListObjectTest.*
    
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

            assertThat(mapper.map(Input(InnerInput(listOf(InnerInnerInput("first"), InnerInnerInput("second"))))))
                .isEqualTo(Output(InnerOutput(listOf(InnerInnerOutput("first"), InnerInnerOutput("second")))))
        }
    }
}