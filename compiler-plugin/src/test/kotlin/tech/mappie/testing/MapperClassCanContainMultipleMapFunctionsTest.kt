package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class MapperClassCanContainMultipleMapFunctionsTest {

    data class Input(val text: String)
    data class Output(val text: String, val int: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapper with multiple map functions should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.MapperClassCanMultipleMapFunctionsTest.*
    
                        class Mapper(private val int: Int) : ObjectMappie<Input, Output>() {
                            fun map(value: String) = value

                            override fun map(from: Input) = mapping {
                                Output::int fromValue int
                            }

                            fun map(value: Int) = value
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
                .call(10)

            assertThat(mapper.map(Input("test")))
                .isEqualTo(Output("test", 10))
        }
    }

}