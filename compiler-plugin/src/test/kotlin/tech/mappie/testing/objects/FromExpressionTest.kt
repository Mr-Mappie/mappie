package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class FromExpressionTest {

    data class Output(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map property fromExpression should succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.FromExpressionTest.*

                class Mapper : ObjectMappie<Unit, Output>() {
                    override fun map(from: Unit) = mapping {
                        Output::value fromExpression { it::class.simpleName!! }
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Unit, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Unit)).isEqualTo(Output(Unit::class.simpleName!!))
        }
    }
}