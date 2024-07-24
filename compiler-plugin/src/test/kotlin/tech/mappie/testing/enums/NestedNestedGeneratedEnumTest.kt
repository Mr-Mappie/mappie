package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class NestedNestedGeneratedEnumTest {

    data class Input(val nested: NestedInput)

    data class NestedInput(val nested: NestedNestedEnumInput)

    enum class NestedNestedEnumInput { A, B, C }

    data class Output(val nested: NestedOutput)

    data class NestedOutput(val nested: NestedNestedEnumOutput)

    enum class NestedNestedEnumOutput { A, B, C }

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapper with multiple map functions should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.enums.NestedNestedGeneratedEnumTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            object NestedMapper : ObjectMappie<NestedInput, NestedOutput>()
                        }
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

            assertThat(mapper.map(Input(NestedInput(NestedNestedEnumInput.A))))
                .isEqualTo(Output(NestedOutput(NestedNestedEnumOutput.A)))
        }
    }
}