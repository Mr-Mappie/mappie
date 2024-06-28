package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class ObjectWithDifferentValuesTest {

    data class Input(val firstname: String, val age: Int)
    data class Output(val name: String, val age: Int)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map two data classes with the different values should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ObjectWithDifferentValuesTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages).containsError("Target Output.name has no source defined")
        }
    }

    @Test
    fun `map two data classes with the different values from KProperty0 should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ObjectWithDifferentValuesTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::name fromProperty from::firstname
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
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("Stefan", 30))).isEqualTo(Output("Stefan", 30))
        }
    }

    @Test
    fun `map two data classes with the different values from KProperty1 should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ObjectWithDifferentValuesTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::name fromProperty Input::firstname
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
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("Sjon", 58))).isEqualTo(Output("Sjon", 58))
        }
    }
}