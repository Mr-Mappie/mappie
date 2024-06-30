package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File
import kotlin.test.Test

class EnumsWithSameEntriesTest {

    enum class Input { SOME, OTHER }
    enum class Output { SOME, OTHER }

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map identical enums should succeed`() {
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
                assertThat(mapper.map(entry)).isEqualTo(Output.valueOf(entry.name))
            }
        }
    }

    @Test
    fun `map identical enums with an explicit mapping should warn`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.EnumsWithSameEntriesTest.*
    
                        class Mapper : EnumMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output.SOME fromEnumEntry Input.SOME
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).containsWarning("Unnecessary explicit mapping of target EnumsWithSameEntriesTest.Output.SOME")

            val mapper = classLoader
                .loadEnumMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            Input.entries.forEach { entry ->
                assertThat(mapper.map(entry)).isEqualTo(Output.valueOf(entry.name))
            }
        }
    }
}
