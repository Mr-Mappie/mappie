package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class GeneratedClassSetTest {
    data class Input(val a: InnerInput)
    data class InnerInput(val value: Set<String>)
    data class Output(val a: InnerOutput)
    data class InnerOutput(val value: Set<String>)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested set without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.GeneratedClassSetTest.*
    
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

            assertThat(mapper.map(Input(InnerInput(setOf("first", "second")))))
                .isEqualTo(Output(InnerOutput(setOf("first", "second"))))
        }
    }
}