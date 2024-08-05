package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File
import kotlin.reflect.full.valueParameters

class MapperClassCanContainAllDeclarationsTest {

    data class Input(val text: String)
    data class Output(val text: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `mapper containing all kind of declarations should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.MapperClassCanContainAllDeclarationsTest.*

                        object IrrelevantObject

                        typealias IrrelevantTypeAlias = String

                        val irrelevantProperty: String = ""

                        class Mapper(private val int: Int) : ObjectMappie<Input, Output>() {
                            
                            constructor(string: String, int: Int) : this(int)
                            
                            init {
                                val x = 10
                            }
                            
                            private val irrelevantInnerProperty: Int = 0

                            object IrrelevantInnerObject

                            fun map(value: String) = value

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
                .first { it.valueParameters.size == 1 }
                .call(10)

            assertThat(mapper.map(Input("test")))
                .isEqualTo(Output("test"))
        }
    }
}