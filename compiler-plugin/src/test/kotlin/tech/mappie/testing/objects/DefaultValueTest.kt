package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class DefaultValueTest {

    @TempDir
    lateinit var directory: File

    @Test
    fun `map two data classes with an unknown target having a default value should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie

                        data class Input(val name: String)
                        data class Output(val name: String, val age: Int = 10)
    
                        class Mapper : ObjectMappie<Input, Output>()
                        """
                    )
                )
            }
        }.compile {
            assertThat(exitCode).isEqualTo(ExitCode.OK)
            assertThat(messages).isEmpty()

            val mapper = classLoader
                .loadObjectMappieClass<Any, Any>("Mapper")
                .constructors
                .first()
                .call()

            val input = classLoader.loadClass("Input").kotlin.constructors.first()
            val output = classLoader.loadClass("Output").kotlin.constructors.first()

            assertThat(mapper.map(input.call("name"))).isEqualTo(output.call("name", 10))
        }
    }

}