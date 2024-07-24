package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class TwoSameGeneratedEnumTest {

    data class FirstInput(val nested: NestedInput)

    data class SecondInput(val nested: NestedInput)

    enum class NestedInput { A, B, C }

    data class FirstOutput(val nested: NestedOutput)

    data class SecondOutput(val nested: NestedOutput)

    enum class NestedOutput { A, B, C }

    @TempDir
    lateinit var directory: File

    @Test
    fun `two mappers generating the same mapper should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.enums.TwoSameGeneratedEnumTest.*
    
                        class FirstMapper : ObjectMappie<FirstInput, FirstOutput>()
                        class SecondMapper : ObjectMappie<SecondInput, SecondOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val firstMapper = classLoader
                .loadObjectMappieClass<FirstInput, FirstOutput>("FirstMapper")
                .constructors
                .first()
                .call()

            assertThat(firstMapper.map(FirstInput(NestedInput.A)))
                .isEqualTo(FirstOutput(NestedOutput.A))


            val secondMapper = classLoader
                .loadObjectMappieClass<SecondInput, SecondOutput>("SecondMapper")
                .constructors
                .first()
                .call()

            assertThat(secondMapper.map(SecondInput(NestedInput.B)))
                .isEqualTo(SecondOutput(NestedOutput.B))
        }
    }
}