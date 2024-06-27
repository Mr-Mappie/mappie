package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class EnumsWithSameEntriesTest {

    enum class Input { SOME, OTHER }
    enum class Output { SOME, OTHER }

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map two enums with the same entries should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.EnumsWithSameEntriesTest.*
    
                        class Mapper : EnumMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadEnumMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            Input.entries.forEach { entry ->
                assertEquals(Output.valueOf(entry.name), mapper.map(entry))
            }
        }
    }
}
