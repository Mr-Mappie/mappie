package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class ViaListTest {
    data class Input(val text: List<InnerInput>)
    data class InnerInput(val value: String)
    data class Output(val text: List<String>)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map via forList should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.ViaListTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::text fromProperty from::text via InnerMapper.forList
                            }
                        }

                        object InnerMapper : ObjectMappie<InnerInput, String>() {
                            override fun map(from: InnerInput) = from.value
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

            assertThat(mapper.map(Input(listOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(listOf("A", "B")))
        }
    }

}