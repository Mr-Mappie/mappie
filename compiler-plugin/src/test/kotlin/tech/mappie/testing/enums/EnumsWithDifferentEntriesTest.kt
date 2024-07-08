package tech.mappie.testing.enums

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.containsError
import tech.mappie.testing.loadEnumMappieClass
import java.io.File
import kotlin.test.Test

class EnumsWithDifferentEntriesTest {

    enum class Input { FIRST, SECOND, THIRD }
    enum class Output { FIRST, SECOND }

    @TempDir
    lateinit var directory: File

    @Test
    fun `map two enums with the different entries should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.enums.EnumsWithDifferentEntriesTest.*
    
                        class Mapper : EnumMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output.FIRST fromEnumEntry Input.THIRD
                            }
                        }
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

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThat(mapper.map(Input.THIRD)).isEqualTo(Output.FIRST)
        }
    }
    @Test
    fun `map two enums with the different entries unmapped to exception should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.enums.EnumsWithDifferentEntriesTest.*
    
                        class Mapper : EnumMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                kotlin.IllegalStateException() thrownByEnumEntry Input.THIRD
                            }
                        }
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

            assertThat(mapper.map(Input.FIRST)).isEqualTo(Output.FIRST)
            assertThat(mapper.map(Input.SECOND)).isEqualTo(Output.SECOND)
            assertThatThrownBy { mapper.map(Input.THIRD) }.isInstanceOf(IllegalStateException::class.java)
        }
    }

    @Test
    fun `map two enums with the different entries should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.EnumMappie
                        import tech.mappie.testing.enums.EnumsWithDifferentEntriesTest.*
    
                        class Mapper : EnumMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages).containsError("Source Input.THIRD has no target defined")
        }
    }
}
