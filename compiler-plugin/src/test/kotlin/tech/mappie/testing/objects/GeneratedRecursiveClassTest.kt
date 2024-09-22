package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GeneratedRecursiveClassTest {
    data class Input(val value: LinkedListInput)
    data class LinkedListInput(val value: String, val next: LinkedListInput?)
    data class Output(val value: LinkedListOutput)
    data class LinkedListOutput(val value: String, val next: LinkedListOutput?)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested recursive class without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.GeneratedRecursiveClassTest.*
    
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

            assertThat(mapper.map(Input(LinkedListInput("a", LinkedListInput("b", null)))))
                .isEqualTo(Output(LinkedListOutput("a", LinkedListOutput("b", null))))
        }
    }
}