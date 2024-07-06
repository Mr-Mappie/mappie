package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class NonNullListToNullListTest {
    data class Input(val text: List<InnerInput>, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: List<InnerOutput>?, val int: Int)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested null list to non-null list using object InnerMapper without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.lists.NonNullListToNullListTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()

                        object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()

                        object SecondInnerMapper : ObjectMappie<InnerOutput, InnerInput>()
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

            assertThat(mapper.map(Input(listOf(InnerInput("first"), InnerInput("second")), 20)))
                .isEqualTo(Output(listOf(InnerOutput("first"), InnerOutput("second")), 20))
        }
    }
}