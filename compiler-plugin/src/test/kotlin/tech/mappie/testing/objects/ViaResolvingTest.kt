package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.containsError
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ViaResolvingTest {
    data class Input(val text: InnerInput, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput, val int: Int)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map without implicit without via should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ViaResolvingTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()

                        class InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
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

            assertThat(mapper.map(Input(InnerInput("inner"), 20)))
                .isEqualTo(Output(InnerOutput("inner"), 20))
        }
    }

    @Test
    fun `map without explicit without via and two inner mappers should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ViaResolvingTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                to::text fromProperty from::text
                            }
                        }

                        class InnerMapperA : ObjectMappie<InnerInput, InnerOutput>()
                        class InnerMapperB : ObjectMappie<InnerInput, InnerOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages).containsError(
                "Multiple mappers resolved to be used in an implicit via",
                listOf(
                    "Explicitly call one of InnerMapperA, InnerMapperB explicitly.",
                    "Delete all except one of InnerMapperA, InnerMapperB.",
                )
            )
        }
    }

    @Test
    fun `map without implicit without via and two inner mappers should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ViaResolvingTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()

                        class InnerMapperA : ObjectMappie<InnerInput, InnerOutput>()
                        class InnerMapperB : ObjectMappie<InnerInput, InnerOutput>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages).containsError(
                "Multiple mappers resolved to be used in an implicit via",
                   listOf(
                       "Explicitly call one of InnerMapperA, InnerMapperB explicitly.",
                       "Delete all except one of InnerMapperA, InnerMapperB.",
                )
            )
        }
    }
}