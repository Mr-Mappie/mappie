package tech.mappie.testing.lists

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.containsError
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class NullToNonNullTest {
    data class Input(val value: String?)
    data class Output(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map data class with null to non-null should fail`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.lists.NullToNonNullTest.*
    
                        class Mapper : ObjectMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.COMPILATION_ERROR)
            assertThat(messages)
                .containsError("Target Output::value automatically resolved from Input::value but cannot assign source type String? to target type String")
        }
    }

    @Test
    fun `map data class with null to non-null should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.lists.NullToNonNullTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::value fromProperty Input::value transform { it ?: "null" }
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

            assertThat(mapper.map(Input(null))).isEqualTo(Output("null"))
            assertThat(mapper.map(Input("value"))).isEqualTo(Output("value"))
        }
    }
}