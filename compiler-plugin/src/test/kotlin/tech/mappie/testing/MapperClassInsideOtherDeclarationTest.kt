package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class MapperClassInsideOtherDeclarationTest {

    data class Input(val text: String)
    data class Output(val text: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapper can be declared nested`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.MapperClassInsideOtherDeclarationTest.*

                        class Thing {
                            class Mapper : ObjectMappie<Input, Output>()                            
                        }
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Thing\$Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input("test")))
                .isEqualTo(Output("test"))
        }
    }
}