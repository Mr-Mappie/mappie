package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ObjectWithNestedClassTest {
    data class Input(val text: InnerInput, val int: Int)
    data class InnerInput(val value: String)
    data class Output(val text: InnerOutput, val int: Int)
    data class InnerOutput(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data classes with nested class without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ObjectWithNestedClassTest.*
    
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

            assertThat(mapper.map(Input(InnerInput("inner"), 20)))
                .isEqualTo(Output(InnerOutput("inner"), 20))
        }
    }

    @Test
    fun `map data classes with nested class using object InnerMapper without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ObjectWithNestedClassTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()

                        object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
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
    fun `map data classes with nested class using class InnerMapper without declaring mapping should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ObjectWithNestedClassTest.*
    
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
    fun `map data classes with nested class should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ObjectWithNestedClassTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::text fromProperty from::text via InnerMapper
                            }
                        }

                        object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
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
    fun `map data classes with nested class and mapper as inner object declaration should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ObjectWithNestedClassTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::text fromProperty from::text via InnerMapper
                            }

                            object InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
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

            assertThat(mapper.map(Input(InnerInput("value"), 30)))
                .isEqualTo(Output(InnerOutput("value"), 30))
        }
    }

    @Test
    fun `map data classes with nested class and mapper as inner class declaration should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.objects.ObjectWithNestedClassTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::text fromProperty from::text via InnerMapper()
                            }

                            class InnerMapper : ObjectMappie<InnerInput, InnerOutput>()
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

            assertThat(mapper.map(Input(InnerInput("value"), 30)))
                .isEqualTo(Output(InnerOutput("value"), 30))
        }
    }
}