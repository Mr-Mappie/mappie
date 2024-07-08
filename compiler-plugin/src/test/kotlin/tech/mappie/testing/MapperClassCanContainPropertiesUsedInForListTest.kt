package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class MapperClassCanContainPropertiesUsedInForListTest {

    data class Input(val text: List<InnerInput>)
    data class InnerInput(val text: String)
    data class Output(val text: List<InnerOutput>)
    data class InnerOutput(val text: String, val int: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map a property in constructor of mapper should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.MapperClassCanContainPropertiesUsedInForListTest.*
    
                        class Mapper : ObjectMappie<Input, Output>() {
                            override fun map(from: Input) = mapping {
                                Output::text fromProperty from::text via InnerMapper(10).forList
                            }
                        }
                        
                        class InnerMapper(private val int: Int): ObjectMappie<InnerInput, InnerOutput>() {
                            override fun map(from: InnerInput) = mapping { 
                                InnerOutput::int fromValue int
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

            assertThat(mapper.map(Input(listOf(InnerInput("test")))))
                .isEqualTo(Output(listOf(InnerOutput("test", 10))))
        }
    }
}