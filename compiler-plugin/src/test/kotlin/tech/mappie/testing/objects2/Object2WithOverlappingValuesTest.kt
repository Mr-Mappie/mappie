package tech.mappie.testing.objects2

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tech.mappie.testing.compilation.compile
import tech.mappie.testing.loadObjectMappie2Class
import java.io.File

class Object2WithOverlappingValuesTest {

    data class Input1(val value: String, val age: Int)
    data class Input2(val age: Int)
    data class Output(val value: String, val age: Int)

    @TempDir
    lateinit var directory: File

    @Test
    fun `map identical data classes should fail`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects2.Object2WithOverlappingValuesTest.*

                class Mapper : ObjectMappie2<Input1, Input2, Output>()
                """
            )
        } satisfies {
            isCompilationError()
            hasErrorMessage("Target Output::age has multiple sources defined")
        }
    }

    @Test
    fun `map identical data classes should with one specified succeed`() {
        compile(directory) {
            file("Test.kt",
                """
                import tech.mappie.api.ObjectMappie2
                import tech.mappie.testing.objects2.Object2WithOverlappingValuesTest.*

                class Mapper : ObjectMappie2<Input1, Input2, Output>() {
                    override fun map(first: Input1, second: Input2) = mapping {
                        to::age fromProperty second::age
                    }
                }
                """
            )
        } satisfies {
            isOk()
            hasNoMessages()

            val mapper = classLoader
                .loadObjectMappie2Class<Input1, Input2, Output>("Mapper")
                .constructors
                .first()
                .call()

            assertThat(mapper.map(Input1("value", 10), Input2(20)))
                .isEqualTo(Output("value", 20)
            )
        }
    }
}