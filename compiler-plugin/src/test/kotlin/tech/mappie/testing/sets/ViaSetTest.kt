package tech.mappie.testing.sets

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class ViaSetTest {
    data class Input(val text: Set<InnerInput>)
    data class InnerInput(val value: String)
    data class Output(val text: Set<String>)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map via forSet should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.sets.ViaSetTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::text fromProperty from::text via InnerMapper.forSet
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

            assertThat(mapper.map(Input(setOf(InnerInput("A"), InnerInput("B")))))
                .isEqualTo(Output(setOf("A", "B")))
        }
    }

}