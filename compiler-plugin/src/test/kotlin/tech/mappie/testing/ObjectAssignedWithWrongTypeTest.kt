package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class ObjectAssignedWithWrongTypeTest {

    data class Input(val value: String)
    data class Output(val value: Int)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `explicit mapping using fromProperty with wrong type should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ObjectAssignedWithWrongTypeTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::value fromProperty from::value
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target Output::value of type Int cannot be assigned from Input::value of type String")
        }
    }

    @Test
    fun `explicit mapping using fromValue with wrong type should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ObjectAssignedWithWrongTypeTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::value fromValue from.value
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target Output::value of type Int cannot be assigned from value of type String")
        }
    }

    @Test
    fun `explicit mapping using fromExpression with wrong type should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ObjectAssignedWithWrongTypeTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::value fromExpression { it.value }
                            }
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target Output::value of type Int cannot be assigned from expression of type String")
        }
    }
}