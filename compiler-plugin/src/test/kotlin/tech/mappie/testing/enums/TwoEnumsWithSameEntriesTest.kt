package tech.mappie.testing.enums

import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadEnumMappieClass
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class TwoEnumsWithSameEntriesTest {

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
                        import tech.mappie.testing.enums.TwoEnumsWithSameEntriesTest.*
    
                        class Mapper : EnumMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertEquals(ExitCode.OK, exitCode)
            assertEquals("", messages)

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
