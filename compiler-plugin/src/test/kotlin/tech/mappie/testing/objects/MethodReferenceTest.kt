package tech.mappie.testing.objects

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappieClass
import java.io.File

class MethodReferenceTest {

    data class Input(val x: Int)
    data class Output(val value: String)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map property fromExpression should succeed with method reference`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.MethodReferenceTest.*

                class Mapper : ObjectMappie<Int, Output>() {
                    override fun map(from: Int) = mapping {
                        Output::value fromExpression Int::toString
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Int, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(101)).isEqualTo(Output("101"))
        }
    }

    @Test
    fun `map property fromProperty should succeed with method reference transform`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie
                import tech.mappie.testing.objects.MethodReferenceTest.*

                class Mapper : ObjectMappie<Input, Output>() {
                    override fun map(from: Input) = mapping {
                        Output::value fromProperty from::x transform Int::toString
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappieClass<Input, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input(101))).isEqualTo(Output("101"))
        }
    }
}