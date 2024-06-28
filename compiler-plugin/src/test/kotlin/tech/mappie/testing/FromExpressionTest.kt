package tech.mappie.testing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.KotlinCompilation
import tech.mappie.testing.compilation.KotlinCompilation.ExitCode
import tech.mappie.testing.compilation.SourceFile.Companion.kotlin
import java.io.File

class FromExpressionTest {

    data class Output(val value: String)

    @TempDir
    private lateinit var directory: File

    @Test
    fun `map value fromExpression should succeed`() {
        KotlinCompilation(directory).apply {
            sources = buildList {
                add(
                    kotlin("Test.kt",
                        """
                        import tech.mappie.api.ObjectMappie
                        import tech.mappie.testing.FromExpressionTest.*
    
                        class Mapper : ObjectMappie<Unit, Output>() {
                            override fun map(from: Unit) = mapping {
                                Output::value fromExpression { it::class.simpleName!! }
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
                .loadObjectMappieClass<Unit, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Unit)).isEqualTo(Output(Unit::class.simpleName!!))
        }
    }
}