package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.google.common.base.Objects
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class ConstructorParameterNotAFieldTest {

    data class Input(val input: String, val age: Int)
    class Output(output: String, val age: Int) {
        val value = output

        override fun equals(other: Any?) = other is Output && value == other.value && age == other.age
        override fun hashCode(): Int = Objects.hashCode(value, age)
    }

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map two classes with unknown parameter set should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ConstructorParameterNotAFieldTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                parameter("fake") fromProperty Input::input
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages).containsError("Parameter fake does not occur as a parameter in constructor")
        }
    }

    @Test
    fun `map two classes with parameter set should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ConstructorParameterNotAFieldTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                parameter("output") fromProperty Input::input
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